# 🚀 Tech Challenge – Fase 4
## Plataforma de Feedback Serverless com PaaS

![Java 17](https://img.shields.io/badge/Java-17-orange)
![Quarkus](https://img.shields.io/badge/Quarkus-3.x-blue)
![Azure](https://img.shields.io/badge/Azure-Cloud-0078D4)
![Terraform](https://img.shields.io/badge/IaC-Terraform-purple)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED)
![CI/CD](https://img.shields.io/badge/GitHub-Actions-green)

---

## 📋 Sobre o Projeto
Este projeto consiste em uma **plataforma de feedback escalável, desacoplada e orientada a eventos**, desenvolvida para 
atender aos requisitos da **Fase 4 do Tech Challenge (FIAP)**.

O objetivo é permitir que **estudantes avaliem aulas** enquanto **administradores recebem notificações de urgência e 
relatórios analíticos periódicos**, utilizando **recursos PaaS (Platform as a Service) da Microsoft Azure** para 
garantir **escalabilidade, disponibilidade e redução de overhead operacional**.

---

## 🎯 Objetivos da Solução
- Coletar feedbacks de forma simples e eficiente
- Processar avaliações urgentes de forma assíncrona
- Enviar notificações automáticas para administradores
- Gerar relatórios analíticos periódicos
- Utilizar **arquitetura cloud native com serviços PaaS**
- Garantir escalabilidade e desacoplamento entre os componentes

---

## 🏛️ Arquitetura da Solução
Todos os componentes da solução são executados como **Web Apps (PaaS)** na Microsoft Azure,
incluindo a API principal e os Workers de Processamento e Relatórios, garantindo
padronização, escalabilidade e menor sobrecarga operacional.

![Arquitetura de Feedback Serverless PaaS](docs/images/arquitetura-feedback-paas.png)


### 🔄 Fluxo de Dados
1. **API (Azure Web App)**  
   Recebe o feedback via HTTP POST, valida os dados e persiste no banco de dados.  
   Caso o feedback seja crítico, uma mensagem é publicada na fila.

2. **Azure Storage Queue**  
   Responsável por desacoplar o recebimento do processamento.

3. **Worker de Processamento (Web App – Background)**  
   Escuta a fila e envia notificações por e-mail (SMTP) quando o feedback é urgente.

4. **Worker de Relatórios (Web App – Scheduled)**  
   Executa periodicamente para consolidar dados e enviar um relatório HTML.

---

## 🛠️ Tecnologias Utilizadas
- **Linguagem:** Java 17
- **Framework:** Quarkus 3.x
- **Cloud (PaaS):** Microsoft Azure
    - Azure Web Apps
    - Azure Database for PostgreSQL (Flexible Server)
    - Azure Storage Queue
- **Infraestrutura como Código:** Terraform
- **Ambiente Local:** Docker Compose
    - PostgreSQL
    - Azurite (Emulador do Azure Storage)
- **CI/CD:** GitHub Actions

---

## 💻 Como Rodar Localmente (Docker)
O projeto possui um **ambiente de desenvolvimento totalmente containerizado**.  
Não é necessário conectar à Azure para execução local, pois são utilizados **emuladores oficiais**.

### ✅ Pré-requisitos
- Docker Desktop instalado e em execução

### 🧱 Arquitetura Local
O arquivo `docker-compose.yml` provisiona:
- PostgreSQL como banco de dados local
- Azurite para simulação do Azure Storage (Queue e Blob)
- Microsserviços: API, Worker de Fila e Worker de Relatórios

### ▶️ Passo a Passo
1. Acesse a pasta `local` do projeto.
2. Execute o comando `docker-compose up --build`.
3. Aguarde até que todos os containers estejam com status **healthy**.

---

## 🌐 Acessos Locais
### API
- Swagger UI: http://localhost:8080/q/swagger-ui

### Banco de Dados
- Host: localhost
- Porta: 5432
- Usuário: admin
- Senha: admin
- Database: tech_challenge_db

### Azurite (Azure Storage Emulator)
- Blob: 10000
- Queue: 10001

> ⚠️ **Nota:**  
> No ambiente local, a variável `QUARKUS_MAILER_MOCK=true` está habilitada.  
> Os e-mails não são enviados de fato, mas o conteúdo HTML pode ser visualizado nos logs dos containers:
> - tech_challenge_worker
> - tech_challenge_relatorio

---

## 🚀 CI/CD e Deploy Automatizado
O projeto utiliza **GitHub Actions** com um fluxo automatizado, seguindo um **GitFlow Simplificado**.

### 🔧 Desenvolvimento (`develop`)
- Todo o desenvolvimento ocorre na branch `develop`
- Ao realizar um push:
    - Validação do Terraform
    - Criação automática de Pull Request para a branch `main`

### 🚀 Produção (`main`)
Após o merge do Pull Request:
- Build da aplicação Java
- Atualização da infraestrutura (se necessário)
- Deploy automático nos Azure Web Apps

📌 Para realizar um deploy, basta realizar um commit na branch `develop`.

---

## 📡 Endpoints da API
### ➕ Criar Avaliação
- Método: POST
- Endpoint: /avaliacao
- URL Produção:  
  https://app-techchallengefeedback.azurewebsites.net/avaliacao

### 📥 Payload (JSON)
```json
{
  "descricao": "A aula de Cloud foi excelente.",
  "nota": 8
}
```

### ⚙️ Comportamento

- **Nota menor que 5**
  - Classificada como **urgente**
  - Persistida no banco de dados
  - Enviada para a fila
  - Worker de processamento dispara notificação por e-mail para o administrador

- **Nota maior ou igual a 5**
  - Persistida no banco de dados
  - Considerada no processamento do **relatório semanal**
  - Não gera notificação imediata

---

## 📝 Documentação dos Workers

### 1️⃣ Worker de Processamento
- **Nome:** tech-challenge-envia-e-mail
- **Tipo:** Web App (PaaS)
- **Função:** Processamento assíncrono de feedbacks urgentes
- **Gatilho:** Mensagens recebidas na fila `feedback-urgente-queue`
- **Ação:**
  - Consome mensagens da fila
  - Processa feedbacks críticos
  - Envia notificação por e-mail ao administrador

---

### 2️⃣ Worker de Relatórios
- **Nome:** tech-challenge-relatorio
- **Tipo:** Web App (PaaS)
- **Função:** Geração de relatórios analíticos periódicos
- **Gatilho:** Execução agendada por tempo (scheduler / cron interno)
- **Fonte de Dados:** Banco de dados PostgreSQL
- **Ação:**
  - Consulta diretamente o banco de dados
  - Consolida as avaliações armazenadas
  - Gera estatísticas, como:
    - Total de avaliações
    - Avaliações urgentes
    - Avaliações não urgentes
  - Envia relatório HTML consolidado ao administrador

---

## ✅ Considerações Finais
Este projeto demonstra, de forma prática, a aplicação de conceitos modernos de desenvolvimento de software, incluindo:

- Arquitetura orientada a eventos  
- Microsserviços desacoplados  
- Aplicações **Cloud Native** com uso intensivo de **PaaS**  
- Infraestrutura como Código (Terraform)  
- Automação completa de CI/CD com GitHub Actions  

Atendendo aos requisitos técnicos e arquiteturais da **Fase 4 do Tech Challenge (FIAP)**, com foco em escalabilidade, 
resiliência e boas práticas de cloud.
