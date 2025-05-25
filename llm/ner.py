from Part2.gemini_strategy import GeminiStrategy
from Part2.entity_extractor import EntityExtractor
import json
import sys

if __name__ == "__main__":
    try:
        # Read input from stdin
        input_json = json.loads(sys.stdin.read())
        article1 = input_json["article1"]
        article2 = input_json["article2"]

        # Create Gemini Strategy
        gemini_strategy = GeminiStrategy('models/gemini-1.5-pro-latest', 'AIzaSyA19TLhE8m7qJuNy5VaKB7Ns7f_ymsWH4I')

        # Use EntityExtractor with Gemini Strategy
        extractor = EntityExtractor(gemini_strategy)
        #print(article1)
        #print(article2)
        # Extract entities
        entities1 = extractor.extract_entities(article1)
        entities2 = extractor.extract_entities(article2)
        #print(entities1)
        #print(entities2)
        # Compute similarity score
        similarity_score = extractor.calculate_similarity(entities1, entities2)

        # Output result as JSON
        output = {"similarity_score": similarity_score}
        print(json.dumps(output))

    except Exception as e:
        # Output error as JSON
        error_output = {"error": str(e)}
        print(json.dumps(error_output), file=sys.stderr)
        sys.exit(1)