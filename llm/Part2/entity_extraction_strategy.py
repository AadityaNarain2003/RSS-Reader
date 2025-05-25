from abc import ABC, abstractmethod
from typing import Set

class EntityExtractionStrategy(ABC):
    @abstractmethod
    def extract_entities(self, article_text: str) -> Set[str]:
        """Extracts named entities from the given text."""
        pass

    @abstractmethod
    def calculate_similarity(self, entities1: Set[str], entities2: Set[str]) -> float:
        """Calculates Jaccard similarity score between two sets of entities."""
        pass
