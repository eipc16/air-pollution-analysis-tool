import json

def load_config(config_path, api_name=None):
    result = {}
    with open(config_path) as config_file:
        result = json.load(config_file)
        if api_name is None:
            return result
        return result[api_name]
    raise Exception(f'Could not load config file from path: {config_path}')

def get_credentials(config):
    credentials = config['credentials']
    if credentials['type'] == 'file':
        return get_credentials_from_file(credentials['path'])
    return (credentials['user'], credentials['password'])

def get_credentials_from_file(credentials_file_path):
    with open(credentials_file_path) as credentials_file:
        credentials = json.load(credentials_file)
        return (credentials['user'], credentials['password'])
    raise Exception(f'Could not load user credentials from path: {credentials_file_path}')

def get_api_key(config):
    credentials = config['credentials']
    if credentials['type'] == 'file':
        return get_api_key_from_file(credentials['path'])
    return credentials['api_key']


def get_api_key_from_file(credentials_file_path):
    with open(credentials_file_path) as credentials_file:
        credentials = json.load(credentials_file)
        return credentials['api_key']
    raise Exception(f'Could not load api key from path: {credentials_file_path}')
