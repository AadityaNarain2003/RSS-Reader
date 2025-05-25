import requests
import json
from dotenv import load_dotenv
import os
import sys
from data_models import Feed, Subscription
from auth_token import AuthToken
from summary_strategy.summary_strategy import SummaryStrategy
from summary_strategy.summary_strategy_openai import OpenAISummaryStrategy

from summary_builder.summary_builder import SummaryBuilder
from summary_builder.subscription import SubscriptionSummaryBuilder

load_dotenv()

if len(sys.argv) == 2:
    path = sys.argv[1]
    os.chdir(path)


# Constants
SYS_PROMPT_SUBSCRIPTION = "You are given a list of articles demarcated by the <article> tag. Summarise these articles retrieved from a subscription. Limit your response to 300 words."
SYS_PROMPT_CATEGORY = "You are given a daily news report for the category {category_name}. You will be given a list of subscriptions and their summaries demarcated by the <subscription> tag. Summarise these subscriptions. Give equal importance to each subscription. Limit your response to 1000 words."



# Functions to interact with APIs
def get_subscriptions() -> dict:
    base_url = "http://localhost:8080/reader-web"
    subscriptions_url = f"{base_url}/api/subscription?unread=false"
    headers = {"Cookie": AuthToken().get_token()}

    try:
        response = requests.get(subscriptions_url, headers=headers)
        return json.loads(response.text)
    except Exception as e:
        print(f"Could not fetch subscriptions: {str(e)}")
        return None


def get_feeds(subscription_id) -> list[Feed]:
    base_url = "http://localhost:8080/reader-web"
    feeds_url = f"{base_url}/api/subscription/{subscription_id}?unread=false&limit=10"
    headers = {"Cookie": AuthToken().get_token()}

    try:
        response = requests.get(feeds_url, headers=headers)
        feeds_data = json.loads(response.text)
        feeds_list = []
        if feeds_data and 'articles' in feeds_data:
            for feed in feeds_data['articles']:
                feeds_list.append(Feed(title=feed['title'], description=feed['description'], date=feed['date']))
        return feeds_list
    except Exception as e:
        print(f"Could not fetch articles for subscription {subscription_id}: {str(e)}")
        return None


# Category Summary Function
def generate_summary_category(category: dict, strategy: SummaryStrategy):
    category_name = category['name']
    builder = SummaryBuilder(strategy)
    builder.reset()

    for subscription in category.get('subscriptions', []):
        subs = Subscription(id=subscription['id'], name=subscription['title'])
        feeds = get_feeds(subs.id)

        subscription_builder = SubscriptionSummaryBuilder(strategy)
        subscription_builder.reset()

        if feeds:
            for feed in feeds:
                subscription_builder.add_feed(feed)
        else:
            subscription_builder.add_section(subs.name, "No feeds found")

        subscription_summary = subscription_builder.build_summary(SYS_PROMPT_SUBSCRIPTION)
        builder.add_section(subs.name, subscription_summary)

    for sub_category in category.get('categories', []):
        sub_category_summary = generate_summary_category(sub_category, strategy)
        builder.add_section(sub_category['name'], sub_category_summary)

    final_summary = builder.build_summary(SYS_PROMPT_CATEGORY.format(category_name=category_name))

    with open("raw_summary.md", "a") as f:
        f.write(f"{category_name}\n---\n{final_summary}\n\n")

    return final_summary


def main():
    auth_token = AuthToken().get_token()
    if not auth_token:
        print("Authentication failed")
        return

    strategy = OpenAISummaryStrategy()
    with open("report.md", "w") as f, open("raw_summary.md", "w") as f1:
        f.write("# Daily Report\n\n")

    subscriptions = get_subscriptions()
    if subscriptions:
        main_category = subscriptions['categories'][0]
        main_category['name'] = 'main'
        final_summary = generate_summary_category(main_category, strategy)
        with open("report.md", "a") as f:
            f.write(final_summary)
    else:
        print("No subscriptions found")


if __name__ == "__main__":
    main()
