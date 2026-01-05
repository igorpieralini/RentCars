"""
Main - Sistema de Gerenciamento de Formações
Arquivo principal que coordena a inicialização do banco de dados
"""

from config_loader import ConfigLoader
from database_manager import DatabaseManager
from data_inserter import DataInserter


def main():
    """Função principal"""
    print("=" * 60)
    print("Sistema de Gerenciamento de Formações")
    print("=" * 60)
    print()
    
    print("1. Carregando configurações...")
    config = ConfigLoader.load_config()
    print(f"   Banco: {config['database']['name']}")
    print(f"   Host: {config['database']['host']}")
    print(f"   Timezone: {config['app']['timezone']}")
    print()
    
    print("2. Conectando ao banco de dados...")
    db = DatabaseManager(config)
    db.connect()
    print()
    
    print("3. Criando banco de dados...")
    db.create_database()
    print()
    
    print("4. Criando tabelas...")
    db.create_tables()
    print()
    
    inserter = DataInserter(db)
    inserter.insert_all()
    
    print("6. Encerrando...")
    db.close()
    
    print()
    print("=" * 60)
    print("✓ Base de dados criada com sucesso!")
    print("=" * 60)


if __name__ == '__main__':
    main()
