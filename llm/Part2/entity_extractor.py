from Part2.entity_extraction_strategy import EntityExtractionStrategy

class EntityExtractor:
    def __init__(self, strategy: EntityExtractionStrategy):
        self.strategy = strategy

    def extract_entities(self, text: str) -> set:
        return self.strategy.extract_entities(text)

    def calculate_similarity(self, entities1, entities2) -> float:
        return self.strategy.calculate_similarity(entities1, entities2)
