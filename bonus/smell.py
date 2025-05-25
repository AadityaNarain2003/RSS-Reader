
import requests
import os
import json

GEMINI_API_KEY = os.environ.get("GEMINI_API_KEY")
GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent"
DEEPSEEK_API_KEY = os.environ.get("DEEPSEEK_API_KEY")
DEEPSEEK_API_URL = "https://openrouter.ai/api/v1/chat/completions"

class SmellDetectionAndRefactoring:
    def __init__(self):
        self.deepseek_api_key = DEEPSEEK_API_KEY
        self.deepseek_api_url = DEEPSEEK_API_URL
        self.gemini_api_key = GEMINI_API_KEY
        self.gemini_api_url = GEMINI_API_URL

    def detect_code_smells(self, file_content, api="deepseek"):
        print(f"[INFO] Starting code smell detection using {api} API...")
        prompt = (
            "Analyze the following Java code for common code smells such as "
            "long methods, duplicated blocks, or inappropriate naming.\n"
            f"Code:\n{file_content}\n"
            "Provide a concise summary in the form of bullet for each potential code smell."
        )

        if api == "deepseek":
            headers = {
                "Authorization": f"Bearer {self.deepseek_api_key}",
                "Content-Type": "application/json",
                "HTTP-Referer": "<YOUR_SITE_URL>",  # Optional
                "X-Title": "<YOUR_SITE_NAME>",  # Optional
            }
            data = {
                "model": "deepseek/deepseek-r1:free",
                "messages": [{"role": "user", "content": prompt}],
            }
            print("[INFO] Sending request to DeepSeek API for code smell analysis...")
            response = requests.post(self.deepseek_api_url, headers=headers, data=json.dumps(data))
        elif api == "gemini":
            data = {
                "contents": [{"parts": [{"text": prompt}]}]
            }
            params = {"key": self.gemini_api_key}
            print("[INFO] Sending request to Gemini API for code smell analysis...")
            # print('\n\n\n')
            response = requests.post(self.gemini_api_url, json=data, params=params)
        else:
            raise ValueError(f"Unsupported API: {api}")

        # Parse the response
        result = response.json()
        # print(result)
        if response.status_code != 200:
            raise Exception(f"[ERROR] API returned an error: {result}")

        if api == "deepseek":
            return result.get("choices", [{}])[0].get("message", {}).get("content", "")
        elif api == "gemini":
            return result.get("candidates", [{}])[0].get("content", {}).get("parts", [{}])[0].get("text", "")

    def detect_design_smells(self, file_content, api="deepseek"):
        print(f"[INFO] Starting design smell detection using {api} API...")
        prompt = (
            "Analyze the following Java code for design smells such as large classes, "
            "god objects, circular dependencies, or poor abstraction.\n"
            f"Code:\n{file_content}\n"
            "Provide a concise summary in the form of bullet for each potential design smell."
        )

        if api == "deepseek":
            headers = {
                "Authorization": f"Bearer {self.deepseek_api_key}",
                "Content-Type": "application/json",
                "HTTP-Referer": "<YOUR_SITE_URL>",  # Optional
                "X-Title": "<YOUR_SITE_NAME>",  # Optional
            }
            data = {
                "model": "deepseek/deepseek-r1:free",
                "messages": [{"role": "user", "content": prompt}],
            }
            print("[INFO] Sending request to DeepSeek API for design smell analysis...")
            response = requests.post(self.deepseek_api_url, headers=headers, data=json.dumps(data))
        elif api == "gemini":
            data = {
                "contents": [{"parts": [{"text": prompt}]}]
            }
            params = {"key": self.gemini_api_key}
            print("[INFO] Sending request to Gemini API for design smell analysis...")
            response = requests.post(self.gemini_api_url, json=data, params=params)
        else:
            raise ValueError(f"Unsupported API: {api}")

        # Parse the response
        result = response.json()
        # print(result)
        # print('\n\n\n')
        if response.status_code != 200:
            raise Exception(f"[ERROR] API returned an error: {result}")

        if api == "deepseek":
            return result.get("choices", [{}])[0].get("message", {}).get("content", "")
        elif api == "gemini":
            return result.get("candidates", [{}])[0].get("content", {}).get("parts", [{}])[0].get("text", "")

    def refactor_code(self, file_content, code_smells_report, design_smells_report, api="deepseek"):
        print(f"[INFO] Starting code refactoring process using {api} API...")
        prompt = (
            "Given the following Java code along with identified code smells and design smells, "
            "refactor the code to address these issues while preserving functionality . give me only fully working java code as output , no explanations.\n"
            f"Code Smells:\n{code_smells_report}\n\n"
            f"Design Smells:\n{design_smells_report}\n\n"
            "Original Code:\n"
            f"{file_content}\n\n"
            "Provide only the working refactored code in correct Java syntax."
        )

        if api == "deepseek":
            headers = {
                "Authorization": f"Bearer {self.deepseek_api_key}",
                "Content-Type": "application/json",
                "HTTP-Referer": "<YOUR_SITE_URL>",  # Optional
                "X-Title": "<YOUR_SITE_NAME>",  # Optional
            }
            data = {
                "model": "deepseek/deepseek-r1:free",
                "messages": [{"role": "user", "content": prompt}],
            }
            print("[INFO] Sending request to DeepSeek API for code refactoring...")
            response = requests.post(self.deepseek_api_url, headers=headers, data=json.dumps(data))
        elif api == "gemini":
            data = {
                "contents": [{"parts": [{"text": prompt}]}]
            }
            params = {"key": self.gemini_api_key}
            print("[INFO] Sending request to Gemini API for code refactoring...")
            response = requests.post(self.gemini_api_url, json=data, params=params)
        else:
            raise ValueError(f"Unsupported API: {api}")

        
        result = response.json()
        # print(result)
        # print('\n\n\n')
        if response.status_code != 200:
            raise Exception(f"[ERROR] API returned an error: {result}")

        if api == "deepseek":
            return result.get("choices", [{}])[0].get("message", {}).get("content", "")
        elif api == "gemini":
            return result.get("candidates", [{}])[0].get("content", {}).get("parts", [{}])[0].get("text", "")