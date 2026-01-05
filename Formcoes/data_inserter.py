import json
from mysql.connector import Error

class DataInserter:
    def __init__(self, db):
        self.db = db
        self.data = self._load_data()

    def _load_data(self):
        """Carrega dados do arquivo JSON"""
        try:
            with open('data_constants.json', 'r', encoding='utf-8') as f:
                return json.load(f)
        except FileNotFoundError:
            print("✗ Erro: Arquivo data_constants.json não encontrado!")
            return {}

    def insert_formations(self):
        """Insere formações (Superior/Inferior)"""
        print("   a) Inserindo formações...")
        count = 0
        try:
            for item in self.data.get('formations', []):
                try:
                    self.db.cursor.execute(
                        "INSERT IGNORE INTO formations (id, name, description) VALUES (%s, %s, %s)",
                        (item['id'], item['name'], item['description'])
                    )
                    count += 1
                except Error as e:
                    print(f"      ✗ Erro ao inserir {item['name']}: {e}")
            self.db.commit()
            print(f"      ✓ {count} formações inseridas")
        except Exception as e:
            print(f"      ✗ Erro geral: {e}")

    def insert_types(self):
        """Insere tipos (Bacharelado/Técnico/Tecnólogo)"""
        print("   b) Inserindo tipos de formação...")
        count = 0
        try:
            for item in self.data.get('types', []):
                try:
                    self.db.cursor.execute(
                        "INSERT IGNORE INTO types (id, name, description) VALUES (%s, %s, %s)",
                        (item['id'], item['name'], item['description'])
                    )
                    count += 1
                except Error as e:
                    print(f"      ✗ Erro ao inserir {item['name']}: {e}")
            self.db.commit()
            print(f"      ✓ {count} tipos inseridos")
        except Exception as e:
            print(f"      ✗ Erro geral: {e}")

    def insert_courses(self):
        """Insere cursos"""
        print("   c) Inserindo cursos...")
        count = 0
        try:
            for item in self.data.get('courses', []):
                try:
                    self.db.cursor.execute(
                        "INSERT IGNORE INTO courses (name, formation_id, type_id) VALUES (%s, %s, %s)",
                        (item['name'], item['formation_id'], item['type_id'])
                    )
                    count += 1
                except Error as e:
                    print(f"      ✗ Erro ao inserir {item['name']}: {e}")
            self.db.commit()
            print(f"      ✓ {count} cursos inseridos")
        except Exception as e:
            print(f"      ✗ Erro geral: {e}")

    def insert_trainings(self):
        """Insere treinamentos"""
        print("   d) Inserindo treinamentos...")
        count = 0
        try:
            for item in self.data.get('trainings', []):
                try:
                    self.db.cursor.execute(
                        "INSERT IGNORE INTO trainings (name, provider, type, category) VALUES (%s, %s, %s, %s)",
                        (item['name'], item['provider'], item['type'], item['category'])
                    )
                    count += 1
                except Error as e:
                    print(f"      ✗ Erro ao inserir {item['name']}: {e}")
            self.db.commit()
            print(f"      ✓ {count} treinamentos inseridos")
        except Exception as e:
            print(f"      ✗ Erro geral: {e}")

    def insert_all(self):
        """Insere todos os dados em sequência"""
        print("📊 Iniciando inserção de dados...")
        self.insert_formations()
        self.insert_types()
        self.insert_courses()
        self.insert_trainings()
        print("✓ Inserção de dados concluída!")
