from fastapi import FastAPI

from investment.rest_api import holdings

app = FastAPI()
app.include_router(holdings.router)


@app.get("/")
def root():
    return {"message": "Hello World"}
