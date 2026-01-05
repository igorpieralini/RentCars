# 🎓 **EducationBase** - Sistema de Gerenciamento de Formações Educacionais

Um sistema robusto e escalável para gerenciar uma vasta base de dados de formações educacionais, cursos e treinamentos profissionais. Conecta estruturas acadêmicas tradicionais com oportunidades de aprendizado contínuo.

---

## ✨ **Características**

- 📚 **Banco de Dados Abrangente**: 600+ cursos de bacharelado, técnico e tecnólogo
- 🔧 **Arquitetura Modular**: Separação clara de responsabilidades
- 🛡️ **Segurança**: Configuração YAML centralizada
- ⚡ **Escalabilidade**: Pronto para crescimento da base de dados
- 📊 **Structured Data**: JSON para fácil manutenção e importação

---

## 🏗️ **Arquitetura do Projeto**

```
EducationBase/
├── config.yml                 # Configurações do banco de dados
├── config_loader.py          # Carregador de configurações YAML
├── database_manager.py       # Gerenciador de conexões e schema MySQL
├── data_inserter.py         # Insertador de dados JSON
├── data_constants.json      # Base de dados em JSON (600+ registros)
├── main.py                  # Script principal de orquestração
├── requirements.txt         # Dependências Python
└── README.md               # Este arquivo
```

---

## 🚀 **Início Rápido**

### **Pré-requisitos**

- Python 3.8+
- MySQL Server 8.0+
- pip (Python package manager)

### **Instalação**

1. **Clone o repositório**
```bash
git clone https://github.com/seu-usuario/EducationBase.git
cd EducationBase
```

2. **Instale as dependências**
```bash
pip install -r requirements.txt
```

3. **Configure o banco de dados**
Edite `config.yml` com suas credenciais MySQL:
```yaml
database:
  host: localhost
  port: 3306
  user: root
  password: sua_senha
  database: peoplecore
```

4. **Execute o sistema**
```bash
python main.py
```

---

## 📊 **Estrutura do Banco de Dados**

### **Tabela: formations** (2 registros)
Níveis educacionais
```
id | name    | description
---|---------|--------------------------------
1  | Superior| Nível de educação superior
2  | Inferior| Nível de educação básico/médio
```

### **Tabela: types** (3 registros)
Tipos de formação
```
id | name      | description
---|-----------|----------------
1  | Bacharelado| Bacharelado
2  | Tecnico   | Técnico
3  | Tecnologo | Tecnólogo
```

### **Tabela: courses** (400+ registros)
Cursos acadêmicos com relacionamento FK
```
id | name                      | formation_id | type_id
---|---------------------------|--------------|--------
1  | Engenharia Civil           | 1            | 1
2  | Medicina                   | 1            | 1
...
```

**Categorias de cursos incluídos:**
- 🔧 Engenharias (30+ especializações)
- 🏥 Saúde (20+ cursos)
- 💼 Gestão e Administração (25+ cursos)
- 💻 Tecnologia da Informação (40+ cursos)
- 🎨 Artes e Design (15+ cursos)
- 🌾 Agrária (15+ cursos)
- 📚 Humanas (30+ cursos)
- 🧪 Exatas (20+ cursos)
- E muitas mais...

### **Tabela: trainings** (200+ registros)
Treinamentos corporativos
```
id | name                         | provider | type         | category
---|------------------------------|----------|--------------|------------------
1  | Python for Everybody         | Udemy    | Curso        | Programação
2  | Machine Learning A-Z         | Udemy    | Curso        | Data Science
...
```

**Provedores incluídos:**
- 🎓 Udemy
- 🎯 Alura
- 🌐 Coursera
- 🏢 Empresa (Treinamentos corporativos)
- 💼 LinkedIn Learning
- 🖥️ Pluralsight
- ☁️ Google Cloud
- ☁️ AWS
- 🔵 Microsoft Azure

---

## 📋 **Conteúdo de Dados**

### **Bacharelados** (100+ cursos)
Engenharia Civil, Software, Elétrica, Química, Medicina, Enfermagem, Direito, Administração, Economia, Psicologia, Educação, História, Geografia, Matemática, Física, Biologia, Ciência da Computação, Arquitetura, Agronomia, e muito mais!

### **Cursos Técnicos** (60+ cursos)
Eletrônica, Programação, Redes, Administração, Eletrotécnica, Segurança do Trabalho, Logística, Gastronomia, Farmácia, Radiologia, Enfermagem, Mecânica, Edificações, e outros.

### **Tecnólogos** (140+ cursos)
Análise de Sistemas, Gestão em TI, Segurança da Informação, Logística, Marketing Digital, Gestão Ambiental, Design Gráfico, Gastronomia, e muitos mais!

### **Treinamentos** (200+ cursos)
Python, JavaScript, React, Angular, Node.js, AWS, Docker, Kubernetes, Machine Learning, Data Science, e mais 150+ treinamentos especializados.

---

## 🔌 **Uso da API**

### **Exemplo 1: Executar o sistema completo**
```bash
python main.py
```
