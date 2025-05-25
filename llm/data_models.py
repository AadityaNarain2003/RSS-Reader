from datetime import datetime


class Feed:
    title: str
    description: str = ""
    date: int = datetime.now().timestamp()

    def __init__(self, title: str, description: str = "", date: int = datetime.now().timestamp()):
        self.title = title
        self.description = description
        self.date = date

    def __str__(self):
        return f"{self.title} : {self.description[:50]}"


class Subscription:
    id: str
    name: str

    def __init__(self, id: str, name: str):
        self.id = id
        self.name = name

    def __str__(self):
        return f"{self.id} : {self.name}"

