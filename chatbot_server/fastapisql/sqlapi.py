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

# Load the CSV data
df = pd.read_csv(".\\personal_transaction.csv")



df['Date'] = pd.to_datetime(df['Date'], format='%m/%d/%Y')

# Define and create the SQLite database
db_path = ".\\sqldb_exp.db"
db_path = f"sqlite:///{db_path}"
engine = create_engine(db_path)
df.to_sql("personal_transactions", engine, index=False, if_exists='replace')
db = SQLDatabase(engine=engine)

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
write_query = create_sql_query_chain(llm, db)
execute_query = db.run  # Execute the generated SQL query

# Combine everything into a single chain
chain = (
    RunnablePassthrough.assign(query=write_query).assign(
        result=write_query | execute_query
    )
    | answer
)

# FastAPI app setup
app = FastAPI()

# Define the request model
class QuestionRequest(BaseModel):
    question: str

# FastAPI route to handle questions
@app.post("/ask")
async def ask_question(request: QuestionRequest):
    try:
        # Get the question from the request body
        user_question = request.question
        
        # Get conversation history from memory
        conversation_history = memory.load_memory_variables({})["history"]
        
        # Invoke the chain with the question and memory history
        response = chain.invoke({
            "question": user_question,
            "history": conversation_history
        })
        
        # Return the response as JSON
        return {"answer": response}

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# To run the FastAPI app, run the following command:
# uvicorn sqlapi:app --reload --port 8001
