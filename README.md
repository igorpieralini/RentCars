# AlugaCar

Sistema de aluguel de carros em Java + MySQL, com interface Swing e envio de email para recuperação de senha.

## 🚀 Quick Start

```shell
git clone https://github.com/seu-usuario/AlugaCar.git
cd AlugaCar
mvn clean install
```

Configure o banco de dados:
- Edite as credenciais em `config.yml` (não versionado)
- Execute: `java -cp target/AlugaCar-1.0-SNAPSHOT.jar me.pieralini.com.Main`

## 📁 Project Structure

src/
├── main/java/me/pieralini/com/
│   ├── Main.java                # Inicialização
│   ├── ui/                     # Telas e componentes
│   │   ├── LoginFrame.java     # Login
│   │   ├── view/               # Cadastros, página principal
│   │   ├── systems/            # Recuperação de senha
│   │   └── components/         # Campos customizados, botões, etc
│   ├── util/                   # Utilitários
│   │   ├── Database.java       # Conexão MySQL
│   │   ├── ConfigLoader.java   # Configuração
│   │   ├── UIHelper.java       # Auxiliares UI
│   │   └── email/              # Email
│   │       ├── EmailConfig.java
│   │       ├── EmailService.java
│   │       └── EmailTemplate.java
├── resources/                  # Imagens, configs

## ⚙️ Features

- Autenticação de usuários
- Cadastro e visualização de carros
- Cards para exibir carros
- Recuperação de senha por email
- Integração com MySQL

## 📊 Database Schema

carros: id, modelo, marca, ano, placa, status
usuarios: id, nome, email, senha

## 🐛 Dependencies

- mysql-connector-java: conexão MySQL
- SnakeYAML: arquivos de configuração
- Jakarta Mail: envio de email

## 📏 License: MIT 🔓 | Author: Igor Pieralini
