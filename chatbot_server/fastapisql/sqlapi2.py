from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import pandas as pd
from sqlalchemy import create_engine
from langchain.memory import ConversationBufferMemory
from langchain.prompts import PromptTemplate
from langchain_community.utilities import SQLDatabase
from langchain_openai import AzureChatOpenAI
from langchain_core.output_parsers import StrOutputParser
from langchain_core.runnables import RunnablePassthrough
from langchain_community.tools.sql_database.tool import QuerySQLDataBaseTool
from langchain.chains import create_sql_query_chain
import os
from dotenv import load_dotenv
import getpass
from fastapi.responses import JSONResponse

import requests

def get_transactions(user_id):
    url = f"http://localhost:8070/transactions/user/{user_id}"
    try:
        response = requests.get(url)
        response.raise_for_status()  # Lève une exception si l'API retourne une erreur HTTP
        transactions = response.json()
        return transactions  # Retourne les transactions sous forme de JSON
    except requests.RequestException as e:
        raise HTTPException(status_code=500, detail=f"Erreur API Spring Boot: {str(e)}")


# Load API credentials
load_dotenv()
if not os.environ.get("AZURE_OPENAI_API_KEY"):
    os.environ["AZURE_OPENAI_API_KEY"] = getpass.getpass("Enter API key for Azure: ")

# Initialize the LLM
llm = AzureChatOpenAI(
    azure_endpoint=os.environ["AZURE_OPENAI_ENDPOINT"],
    azure_deployment=os.environ["AZURE_OPENAI_DEPLOYMENT_NAME"],
    openai_api_version=os.environ["AZURE_OPENAI_API_VERSION"],
)

# Add memory for conversation history
memory = ConversationBufferMemory(return_messages=True)

# Define the prompt template
answer_prompt = PromptTemplate.from_template(
    """
    You are a personal finance assistant. Use the following context to answer questions:

    Conversation History:
    {history}

    User Question:
    {question}

    SQL Query:
    {query}

    SQL Result:
    {result}

    Provide a personalized and actionable response.
    """
)

# Define the answer processing chain
answer = answer_prompt | llm | StrOutputParser()

# SQL query tool setup
#write_query = create_sql_query_chain(llm, db)
#execute_query = db.run  # Execute the generated SQL query

# Combine everything into a single chain
chain = answer

# FastAPI app setup
app = FastAPI()

# Define the request model
class QuestionRequest(BaseModel):
    question: str

# FastAPI route to handle questions
@app.post("/ask/{user_id}")
async def ask_question(user_id: int, request: QuestionRequest):
    try:
        user_question = request.question
        conversation_history = memory.load_memory_variables({})["history"]

        # 🔹 Récupérer les transactions du compte via l’API Spring Boot
        transactions = get_transactions(user_id)

        # 🔹 Convertir en format exploitable pour LangChain
        # transactions_text = "\n".join([f"{t['date']} - {t['category']} : {t['amount']}€" for t in transactions])
        transactions_text = "\n".join([f"{t['transactionDate']} - {t['categoryName']} : {t['amount']}€" for t in transactions])


        # 🔹 Modifier la question pour inclure les transactions
        context = f"Transactions récentes:\n{transactions_text}\n\nQuestion utilisateur: {user_question}"

        # 🔹 Interroger le modèle LangChain avec ce contexte
        response = chain.invoke({
            "question": context,
            "history": conversation_history,
            "query": "",  # Ajoute une valeur par défaut pour éviter l'erreur
            "result": ""  # Ajoute une valeur par défaut pour éviter l'erreur
        })

        # return {"answer": response}
        return JSONResponse(content={"answer": response})

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# To run the FastAPI app, run the following command:
# uvicorn sqlapi2:app --reload --port 8001
