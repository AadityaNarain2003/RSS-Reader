import requests


class AuthToken:
    _instance = None

    def __new__(cls):
        if cls._instance is None:
            cls._instance = super(AuthToken, cls).__new__(cls)
            # Don't initialize attributes here
        return cls._instance

    def __init__(self):
        # Initialize attributes in __init__ (will only run once due to singleton pattern)
        if not hasattr(self, "_initialized"):
            self._token = None
            self._initialize_token()
            self._initialized = True

    def _initialize_token(self):
        """Initialize the token during singleton creation"""
        base_url = "http://localhost:8080/reader-web"
        login_url = f"{base_url}/api/user/login"

        # Login credentials
        credentials = {
            "username": "user1",
            "password": "12345678"
        }

        try:
            # Make login request
            auth_resp = requests.post(login_url, data=credentials)
            auth_token = auth_resp.headers['Set-Cookie'].split(';')[0]
            self._token = auth_token
        except Exception as e:
            print(f"Could not retrieve auth token: {str(e)}")
            self._token = None

    def get_token(self):
        """Return the authentication token"""
        return self._token
