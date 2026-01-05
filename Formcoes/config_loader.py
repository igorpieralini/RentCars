import yaml
import sys


class ConfigLoader:
    """Carrega as configurações do arquivo config.yml"""
    
    @staticmethod
    def load_config(config_path='config.yml'):
        """
        Carrega configurações do arquivo YAML
        
        Args:
            config_path: Caminho do arquivo de configuração
            
        Returns:
            dict: Configurações carregadas
        """
        try:
            with open(config_path, 'r', encoding='utf-8') as file:
                config = yaml.safe_load(file)
            return config
        except FileNotFoundError:
            print(f"Erro: Arquivo {config_path} não encontrado!")
            sys.exit(1)
        except yaml.YAMLError as e:
            print(f"Erro ao ler config.yml: {e}")
            sys.exit(1)
