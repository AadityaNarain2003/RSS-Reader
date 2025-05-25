
import requests

class SmellDetectionAndRefactoring:
    def __init__(self, gemini_api_key, gemini_api_url):
        self.gemini_api_key = gemini_api_key
        self.gemini_api_url = gemini_api_url

    def detect_code_smells(self, file_content):
        print("[INFO] Starting code smell detection...")
        prompt = (
            "Analyze the following Java code for common code smells such as "
            "long methods, duplicated blocks, or inappropriate naming.\n"
            f"Code:\n{file_content}\n"
            "Provide a concise summary of potential code smells."
        )
        data = {
            "contents": [{
                "parts": [{
                    "text": prompt
                }]
            }]
        }
        params = {
            "key": self.gemini_api_key
        }
        print("[INFO] Sending request to Gemini API for code smell analysis...")
        response = requests.post(self.gemini_api_url, json=data, params=params)
        
        # debugging
        # print("[DEBUG] Full API response (Code Smells):", response.text)
        
        result = response.json()
        print("[SUCCESS] Code smell analysis completed")
        return result.get("candidates", [{}])[0].get("content", {}).get("parts", [{}])[0].get("text", "")

    def detect_design_smells(self, file_content):
        print("[INFO] Starting design smell detection...")
        prompt = (
            "Analyze the following Java code for design smells such as large classes, "
            "god objects, circular dependencies, or poor abstraction.\n"
            f"Code:\n{file_content}\n"
            "Provide a concise summary of potential design smells."
        )
        data = {
            "contents": [{
                "parts": [{
                    "text": prompt
                }]
            }]
        }
        params = {
            "key": self.gemini_api_key
        }
        print("[INFO] Sending request to Gemini API for design smell analysis...")
        response = requests.post(self.gemini_api_url, json=data, params=params)
        
        # debugging
        # print("[DEBUG] Full API response (Design Smells):", response.text)
        
        result = response.json()
        print("[SUCCESS] Design smell analysis completed")
        return result.get("candidates", [{}])[0].get("content", {}).get("parts", [{}])[0].get("text", "")

    def refactor_code(self, file_content, code_smells_report, design_smells_report):
        print("[INFO] Starting code refactoring process...")
        prompt = (
            "Given the following Java code along with identified code smells and design smells, "
            "refactor the code to address these issues while preserving functionality.\n"
            f"Code Smells:\n{code_smells_report}\n\n"
            f"Design Smells:\n{design_smells_report}\n\n"
            "Original Code:\n"
            f"{file_content}\n\n"
            "Provide only the working refactored code in correct java syntax."
        )
        data = {
            "contents": [{
                "parts": [{
                    "text": prompt
                }]
            }]
        }
        params = {
            "key": self.gemini_api_key
        }
        print("[INFO] Sending request to Gemini API for code refactoring...")
        response = requests.post(self.gemini_api_url, json=data, params=params)
        
        # debugging
        # print("[DEBUG] Full API response (Refactoring):", response.text)
        
        result = response.json()
        print("[SUCCESS] Code refactoring completed")
        return result.get("candidates", [{}])[0].get("content", {}).get("parts", [{}])[0].get("text", "")




