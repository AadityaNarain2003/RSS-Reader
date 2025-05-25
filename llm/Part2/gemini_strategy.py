import google.generativeai as genai
import json
from Part2.entity_extraction_strategy import EntityExtractionStrategy

class GeminiStrategy(EntityExtractionStrategy):
    def __init__(self, model_name, api_key):
        genai.configure(api_key=api_key)
        self.model = genai.GenerativeModel(model_name)

    def extract_entities(self, article_text: str) -> set:
        """Extracts named entities using Gemini API with improved prompt."""
        prompt = f"""
        Extract all named entities from the following text.
        Include entities like PERSON, ORGANIZATION, LOCATION, PRODUCT, EVENT, etc.
        
        They can be vaguly related also, doesn't have to be the exact match
        
        Break two words into different words, split via space and make everything lowercase
        
        Return the result as a **valid JSON array** of strings.
        Example output: ["Apple Inc.", "Steve Jobs Theater", "Cupertino"]

        Text:
        {article_text}
        """

        try:
            response = self.model.generate_content(prompt)
            entities = response.text.strip().strip("```json").strip("```").strip()
            entities = json.loads(entities)
            return set(map(str.lower, entities))  # Normalize for consistency
        except json.JSONDecodeError:
            print("Error decoding Gemini response. The model did not return valid JSON.")
            print("Response received:", response.text)
            return set()
        except Exception as e:
            print(f"Error during Gemini API call: {e}")
            return set()

    def calculate_similarity(self, entities1, entities2) -> float:
        """Calculates Jaccard similarity score based on entity overlap."""
        intersection = entities1 & entities2
        union = entities1 | entities2

        if not union:  # Handle empty entity sets
            return 0.0
        
        similarity_score = len(intersection) / len(union)
        return similarity_score
