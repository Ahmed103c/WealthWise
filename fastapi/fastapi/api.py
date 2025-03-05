from fastapi import FastAPI
from pydantic import BaseModel
import joblib
import requests
from sentence_transformers import SentenceTransformer

# Initialisation de FastAPI
app = FastAPI()

# Charger le modèle de classification et l'encodeur de labels
classifier = joblib.load("model.pkl")
label_encoder = joblib.load("label_encoder.pkl")

# Charger le modèle d'embedding
embedding_model = SentenceTransformer("all-MiniLM-L6-v2")

# URL du backend Spring Boot
SPRING_BOOT_URL = "http://localhost:8070/api/category/predict"

# Définition de la structure de la requête
class Transaction(BaseModel):
    description: str

def send_to_spring_boot(description, category):
    """ Envoie les résultats au backend Spring Boot """
    data = {"description": description, "predictedCategory": category}
    response = requests.post(SPRING_BOOT_URL, json=data)
    return response.status_code, response.text

@app.post("/predict")
async def predict_category(transaction: Transaction):
    """ Prédit la catégorie et envoie les résultats à Spring Boot """
    # Transformer la description en embedding
    embedding = embedding_model.encode([transaction.description])

    # Faire la prédiction
    prediction = classifier.predict(embedding)

    # Convertir l'index en catégorie
    category = label_encoder.inverse_transform(prediction)[0]

    # Envoyer à Spring Boot
    status, message = send_to_spring_boot(transaction.description, category)

    return {
        "description": transaction.description,
        "predicted_category": category,
        "spring_boot_response": message
    }

# Pour lancer l'API : uvicorn api:app --reload
