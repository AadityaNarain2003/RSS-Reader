"""
This module contains the abstract class for the summary generation strategy.
"""


class SummaryStrategy:
    """
    Abstract base class that defines the interface for summary generation strategies.

    This class implements the Strategy design pattern for text summarization,
    allowing different summarization algorithms to be interchangeable.
    Concrete subclasses must implement the generate_summary method.
    """

    def generate_summary(self, prompt: str, content: str) -> str:
        """
        Generate a summary based on the given prompt and content.

        Args:
            prompt (str): Instructions or context for how the summary should be generated
            content (str): The text content to be summarized

        Returns:
            str: The generated summary text
        """
        raise NotImplementedError("Subclasses must implement generate_summary")
