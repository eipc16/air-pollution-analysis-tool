## Credentials and Authorization

This section contains information about authentication

### Username and password authentication

You can setup credentials by either specifing them in the the `config.json` file like this:

    {
        "<API_NAME>": {
            "credentials": {
                "user": "<USER_NAME>",
                "password": "<PASSWORD>"
            },
            ...
    }

or you can load them from a different file like this:

    {
        "<API_NAME>": {
            "credentials": {
                "type": "file",
                "path": "<RELATIVE_PATH_TO_FILE>"
            },
            ...
    }

Referenced credential file should look like this:

    {
        "user": "<USER_NAME>",
        "password": "<PASSWORD>"
    }
