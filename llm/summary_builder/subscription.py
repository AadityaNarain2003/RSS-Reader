from summary_builder.summary_builder import SummaryBuilder
from summary_strategy.summary_strategy import SummaryStrategy
from data_models import Feed

# Subscription Summary Builder


class SubscriptionSummaryBuilder(SummaryBuilder):
    """
    Specialized builder for creating summaries of subscription feeds.

    This class extends the base SummaryBuilder to provide functionality
    specifically designed for handling feed content from subscriptions.
    It formats feed entries with special <article> tags to allow the
    summarization strategy to identify individual articles in the input.

    Attributes:
        Inherits all attributes from SummaryBuilder:
        - strategy (SummaryStrategy): The strategy used for generating the final summary
        - summary (str): Accumulated text content before final summarization
    """

    def __init__(self, strategy: SummaryStrategy):
        """
        Initialize the SubscriptionSummaryBuilder with a summarization strategy.

        Args:
            strategy (SummaryStrategy): The strategy to use for generating summaries
        """
        super().__init__(strategy)

    def add_feed(self, feed: Feed):
        """
        Add a feed entry to the summary with proper article tagging.

        This method wraps the feed content in <article> tags to help the
        summarization strategy identify individual articles. It includes
        both the title and description of the feed.

        Args:
            feed (Feed): A Feed object containing title, description, and date
        """
        self.summary += f"<article>{feed.title}\n{feed.description}</article>\n\n"
