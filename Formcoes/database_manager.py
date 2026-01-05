import mysql.connector
from mysql.connector import Error
import sys


class DatabaseManager:
    """Gerencia a conexão e operações do banco de dados"""
    
    def __init__(self, config):
        """
        Inicializa o gerenciador de banco de dados
        
        Args:
            config: Dicionário com configurações do banco
        """
        self.config = config['database']
        self.connection = None
        self.cursor = None
    
    def connect(self):
        """Conecta ao banco de dados"""
        try:
            self.connection = mysql.connector.connect(
                host=self.config['host'],
                port=self.config['port'],
                user=self.config['user'],
                password=self.config['password'] or None,
                charset=self.config['charset']
            )
            self.cursor = self.connection.cursor()
            print("✓ Conexão com banco de dados estabelecida!")
        except Error as e:
            print(f"✗ Erro ao conectar: {e}")
            sys.exit(1)
    
    def create_database(self):
        """Cria o banco de dados se não existir"""
        try:
            self.cursor.execute(f"CREATE DATABASE IF NOT EXISTS {self.config['name']}")
            self.cursor.execute(f"USE {self.config['name']}")
            self.connection.commit()
            print(f"✓ Banco de dados '{self.config['name']}' pronto!")
        except Error as e:
            print(f"✗ Erro ao criar banco de dados: {e}")
            sys.exit(1)
    
    def create_tables(self):
        """Cria as tabelas necessárias"""
        try:
            # Tabela: Formations (Superior/Inferior)
            self.cursor.execute("""
                CREATE TABLE IF NOT EXISTS formations (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(100) NOT NULL UNIQUE,
                    description TEXT
                )
            """)
            
            # Tabela: Types (Bacharelado, Tecnico, Tecnologo)
            self.cursor.execute("""
                CREATE TABLE IF NOT EXISTS types (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(100) NOT NULL UNIQUE,
                    description TEXT
                )
            """)
            
            # Tabela: Courses (Cursos)
            self.cursor.execute("""
                CREATE TABLE IF NOT EXISTS courses (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(255) NOT NULL,
                    formation_id INT NOT NULL,
                    type_id INT NOT NULL,
                    FOREIGN KEY (formation_id) REFERENCES formations(id),
                    FOREIGN KEY (type_id) REFERENCES types(id),
                    UNIQUE KEY unique_course (name, formation_id, type_id)
                )
            """)
            
            # Tabela: Trainings (Treinamentos e Cursos Online)
            self.cursor.execute("""
                CREATE TABLE IF NOT EXISTS trainings (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(255) NOT NULL,
                    provider VARCHAR(100) NOT NULL,
                    type VARCHAR(50) NOT NULL,
                    category VARCHAR(100) NOT NULL
                )
            """)
            
            self.connection.commit()
            print("✓ Tabelas criadas com sucesso!")
        except Error as e:
            print(f"✗ Erro ao criar tabelas: {e}")
            sys.exit(1)
    
    def execute_insert(self, query, data):
        """
        Executa um INSERT seguro
        
        Args:
            query: Query SQL com placeholders
            data: Dados para inserir
            
        Returns:
            bool: True se bem-sucedido
        """
        try:
            self.cursor.execute(query, data)
            return True
        except Error as e:
            return False
    
    def commit(self):
        """Confirma transação"""
        self.connection.commit()
    
    def close(self):
        """Fecha a conexão"""
        if self.cursor:
            self.cursor.close()
        if self.connection:
            self.connection.close()
        print("✓ Conexão encerrada!")
