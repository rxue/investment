import uvicorn

uvicorn.run("investment.rest_api:app", host="0.0.0.0", port=8000, reload=True)
