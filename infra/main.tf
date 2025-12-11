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

# Resource Group
resource "azurerm_resource_group" "rg" {
  name     = "rg-${var.app_name}"
  location = var.location
}

# Banco de Dados Postgres
resource "azurerm_postgresql_flexible_server" "db_server" {
  name                = "psql-${var.app_name}"
  resource_group_name = azurerm_resource_group.rg.name
  location            = azurerm_resource_group.rg.location
  version             = "13"

  administrator_login    = "psqladmin"
  administrator_password = var.db_password

  storage_mb   = 32768
  storage_tier = "P4"
  sku_name     = "B_Standard_B1ms"

  zone = "1"
}

# Database
resource "azurerm_postgresql_flexible_server_database" "db" {
  name      = "feedbackdb"
  server_id = azurerm_postgresql_flexible_server.db_server.id
  collation = "en_US.utf8"
  charset   = "utf8"
}

# Regra de Firewall (Libera acesso interno Azure)
resource "azurerm_postgresql_flexible_server_firewall_rule" "allow_azure" {
  name             = "AllowAzureServices"
  server_id        = azurerm_postgresql_flexible_server.db_server.id
  start_ip_address = "0.0.0.0"
  end_ip_address   = "0.0.0.0"
}

# App Service Plan (Linux B1)
resource "azurerm_service_plan" "app_plan" {
  name                = "plan-${var.app_name}"
  resource_group_name = azurerm_resource_group.rg.name
  location            = azurerm_resource_group.rg.location
  os_type             = "Linux"
  sku_name            = "B1"
}

# Web App
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

  # Configurações de ambiente
  app_settings = {
    "DB_URL"            = "jdbc:postgresql://${azurerm_postgresql_flexible_server.db_server.fqdn}:5432/feedbackdb"
    "DB_USER"           = "psqladmin"
    "DB_PASSWORD"       = var.db_password
    "WEBSITES_PORT"     = "8080"
    "QUARKUS_HTTP_PORT" = "8080"
    "WEBSITES_CONTAINER_START_TIME_LIMIT" = "1800"
  }
}