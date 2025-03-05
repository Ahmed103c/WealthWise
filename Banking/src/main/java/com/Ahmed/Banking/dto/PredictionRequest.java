package com.Ahmed.Banking.dto;

public class PredictionRequest {
    private String description;
    private String predictedCategory;

    public PredictionRequest() {}

    public PredictionRequest(String description, String predictedCategory) {
        this.description = description;
        this.predictedCategory = predictedCategory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPredictedCategory() {
        return predictedCategory;
    }

    public void setPredictedCategory(String predictedCategory) {
        this.predictedCategory = predictedCategory;
    }
}
