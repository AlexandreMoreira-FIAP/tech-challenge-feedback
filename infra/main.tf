terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 3.0"
    }
  }
  backend "azurerm" {}
}

provider "azurerm" {
  features {}
}

resource "azurerm_resource_group" "rg" {
  name     = "rg-${var.app_name}"
  location = var.location
}

resource "azurerm_log_analytics_workspace" "analytics" {
  name                = "log-${var.app_name}"
  location            = azurerm_resource_group.rg.location
  resource_group_name = azurerm_resource_group.rg.name
  sku                 = "PerGB2018"
  retention_in_days   = 30
}

resource "azurerm_application_insights" "app_insights" {
  name                = "insights-${var.app_name}"
  location            = azurerm_resource_group.rg.location
  resource_group_name = azurerm_resource_group.rg.name
  workspace_id        = azurerm_log_analytics_workspace.analytics.id
  application_type    = "java"
}

resource "azurerm_storage_account" "sa_app" {
  name                     = "st${replace(var.app_name, "-", "")}2"
  resource_group_name      = azurerm_resource_group.rg.name
  location                 = azurerm_resource_group.rg.location
  account_tier             = "Standard"
  account_replication_type = "LRS"
}

resource "azurerm_storage_queue" "queue" {
  name                 = "feedback-urgente-queue"
  storage_account_name = azurerm_storage_account.sa_app.name
}

resource "azurerm_postgresql_flexible_server" "db_server" {
  name                   = "psql-${var.app_name}"
  resource_group_name    = azurerm_resource_group.rg.name
  location               = azurerm_resource_group.rg.location
  version                = "13"
  administrator_login    = "psqladmin"
  administrator_password = var.db_password
  storage_mb             = 32768
  storage_tier           = "P4"
  sku_name               = "B_Standard_B1ms"
  zone                   = "1"
}

resource "azurerm_postgresql_flexible_server_database" "db" {
  name      = "feedbackdb"
  server_id = azurerm_postgresql_flexible_server.db_server.id
  collation = "en_US.utf8"
  charset   = "utf8"
}

resource "azurerm_postgresql_flexible_server_firewall_rule" "allow_azure" {
  name             = "AllowAzureServices"
  server_id        = azurerm_postgresql_flexible_server.db_server.id
  start_ip_address = "0.0.0.0"
  end_ip_address   = "0.0.0.0"
}

resource "azurerm_service_plan" "app_plan" {
  name                = "plan-${var.app_name}"
  resource_group_name = azurerm_resource_group.rg.name
  location            = azurerm_resource_group.rg.location
  os_type             = "Linux"
  sku_name            = "B1"
}

# --- FUNCTION APP (WORKER) ---
resource "azurerm_linux_function_app" "fn_app" {
  name                = "func-${var.app_name}"
  resource_group_name = azurerm_resource_group.rg.name
  location            = azurerm_resource_group.rg.location
  service_plan_id     = azurerm_service_plan.app_plan.id

  storage_account_name       = azurerm_storage_account.sa_app.name
  storage_account_access_key = azurerm_storage_account.sa_app.primary_access_key

  site_config {
    application_stack {
      java_version = "17"
    }
    app_command_line = "/usr/lib/jvm/msft-17-x64/bin/java -jar /home/site/wwwroot/app.jar"
  }

  app_settings = {
    # Banco
    "DB_URL"      = "jdbc:postgresql://${azurerm_postgresql_flexible_server.db_server.fqdn}:5432/feedbackdb"
    "DB_USER"     = "psqladmin"
    "DB_PASSWORD" = var.db_password

    # Filas (Obrigatórios Azure + Java)
    "AzureWebJobsStorage"     = azurerm_storage_account.sa_app.primary_connection_string
    "AZURE_CONNECTION_STRING" = azurerm_storage_account.sa_app.primary_connection_string
    "QUEUE_NAME"              = azurerm_storage_queue.queue.name

    # E-mail
    "QUARKUS_MAILER_FROM"      = var.email_user
    "QUARKUS_MAILER_HOST"      = "smtp.gmail.com"
    "QUARKUS_MAILER_PORT"      = "587"
    "QUARKUS_MAILER_STARTTLS"  = "REQUIRED"
    "QUARKUS_MAILER_USERNAME"  = var.email_user
    "QUARKUS_MAILER_PASSWORD"  = var.email_password
    "QUARKUS_MAILER_MOCK"      = "false"
    "EMAIL_DESTINATARIO_ADMIN" = replace(var.email_user, "@", "+teste@")

    # Monitoramento
    "APPLICATIONINSIGHTS_CONNECTION_STRING" = azurerm_application_insights.app_insights.connection_string
    "FUNCTIONS_WORKER_RUNTIME"              = "java"
    "FUNCTIONS_EXTENSION_VERSION"           = "~4"
  }
}

# --- WEB APP (API) ---
resource "azurerm_linux_web_app" "app" {
  name                = "app-${var.app_name}"
  resource_group_name = azurerm_resource_group.rg.name
  location            = azurerm_service_plan.app_plan.location
  service_plan_id     = azurerm_service_plan.app_plan.id

  site_config {
    application_stack {
      java_server         = "JAVA"
      java_server_version = "17"
      java_version        = "17"
    }
    app_command_line = "java -jar /home/site/wwwroot/app.jar"
  }

  app_settings = {
    "DB_URL"      = "jdbc:postgresql://${azurerm_postgresql_flexible_server.db_server.fqdn}:5432/feedbackdb"
    "DB_USER"     = "psqladmin"
    "DB_PASSWORD" = var.db_password

    "WEBSITES_PORT"                     = "80"
    "QUARKUS_HTTP_PORT"                 = "80"
    "QUARKUS_SWAGGER_UI_ALWAYS_INCLUDE" = "true"
    "WEBSITES_CONTAINER_START_TIME_LIMIT" = "1800"

    "AZURE_CONNECTION_STRING" = azurerm_storage_account.sa_app.primary_connection_string
    "QUEUE_NAME"              = azurerm_storage_queue.queue.name

    "APPLICATIONINSIGHTS_CONNECTION_STRING" = azurerm_application_insights.app_insights.connection_string
  }
}