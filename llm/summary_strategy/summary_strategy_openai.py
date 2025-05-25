import openai
from summary_strategy.summary_strategy import SummaryStrategy

class OpenAISummaryStrategy(SummaryStrategy):
    """
    Concrete implementation of SummaryStrategy that uses OpenAI's GPT models.
    
    This strategy implements the text summarization interface using OpenAI's API,
    specifically the GPT-4o-mini model by default. It sends the content along with
    a system prompt to OpenAI and returns the generated summary.
    
    Attributes:
        None
        
    Example:
        ```python
        strategy = OpenAISummaryStrategy()
        summary = strategy.generate_summary(
            prompt="Summarize this news article in 3 sentences.",
            content="Long news article text..."
        )
        ```
    """
    
    def generate_summary(self, prompt: str, content: str) -> str:
        """
        Generate a summary using OpenAI's API.
        
        This method sends the provided content to OpenAI's chat completions API
        along with the system prompt that guides the summarization process.
        
        Args:
            prompt (str): System instructions for how the summary should be generated
            content (str): The text content to be summarized
            
        Returns:
            str: The generated summary from the OpenAI model
            
        Raises:
            openai.OpenAIError: If there's an issue with the OpenAI API request
        """
        client = openai.OpenAI()
        response = client.chat.completions.create(
            model="gpt-4o-mini",
            messages=[
                {"role": "system", "content": prompt},
                {"role": "user", "content": content},
            ],
        )
        return response.choices[0].message.content
