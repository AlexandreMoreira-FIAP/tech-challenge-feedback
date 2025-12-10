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