from summary_strategy.summary_strategy import SummaryStrategy


class SummaryBuilder:
    """
    Builder pattern implementation for constructing text summaries.

    This class follows the Builder design pattern to progressively construct
    a complex summary document. It uses a strategy object for the actual
    summarization, allowing for different summarization algorithms to be
    used interchangeably.

    The builder accumulates content in sections with titles, and when
    build_summary() is called, it sends the accumulated content to the
    strategy for final summarization.

    Attributes:
        strategy (SummaryStrategy): The strategy used for generating the final summary
        summary (str): Accumulated text content before final summarization
    """

    def __init__(self, strategy: SummaryStrategy):
        """
        Initialize the SummaryBuilder with a summarization strategy.

        Args:
            strategy (SummaryStrategy): The strategy to use for generating summaries
        """
        self.strategy = strategy
        self.summary = ""

    def reset(self):
        """
        Reset the accumulated summary text to an empty string.

        This method clears any previously added content, allowing the builder
        to be reused for creating a new summary.
        """
        self.summary = ""

    def add_section(self, title: str, content: str):
        """
        Add a new section to the summary with a title and content.

        This method formats the section with Markdown syntax, using ## for
        section titles.

        Args:
            title (str): The heading for the section
            content (str): The content text for the section
        """
        self.summary += f"\n## {title}\n{content}\n"

    def build_summary(self, prompt: str) -> str:
        """
        Generate the final summary using the configured strategy.

        This method sends the accumulated content to the strategy along with
        a prompt that guides how the summarization should be performed.

        Args:
            prompt (str): Instructions for how the summary should be generated

        Returns:
            str: The final generated summary
        """
        return self.strategy.generate_summary(prompt, self.summary)
