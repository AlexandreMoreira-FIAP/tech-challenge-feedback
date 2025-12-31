variable "app_name" {
  default = "techchallengefeedback"
}

variable "location" {
  default     = "southafricanorth"
}

variable "db_password" {
  description = "Senha do administrador do banco de dados"
  type        = string
  sensitive   = true
}

variable "email_user" {
  description = "Endereço de e-mail do Gmail"
  type        = string
  sensitive   = true
}

variable "email_password" {
  description = "Senha de Aplicativo do Gmail"
  type        = string
  sensitive   = true
}