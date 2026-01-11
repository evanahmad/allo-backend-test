# **Finance Aggregator Service**

A high-performance, reactive Spring Boot service designed to aggregate financial data from the Frankfurter API. This project demonstrates advanced architectural patterns, including the Strategy Pattern, Custom Factory Beans, and Immutable In-Memory Storage.
🚀 Features

    Real-time & Historical Data: Fetching latest IDR rates, historical time-series (1999-01-04), and supported currency lists.
    Unified Response Format: All endpoints return a consistent, polymorphic JSON structure.
    Automated Data Ingestion: Data is fetched once during startup and served directly from memory.
    Dynamic Spread Calculation: Proprietary logic for calculating currency spreads based on GitHub username Unicode summation.
    Strict Validation & Robustness: Standardized error codes (AGG-XXX) and global exception handling.

## **Architectural Rationale**

This project adheres to the specific constraints outlined in the technical requirements:

### i. Strategy Pattern (Constraint A)

Instead of using monolithic if-else or switch blocks, we implemented the Strategy Pattern. Each data type (latest, historical, currencies) is handled by a dedicated class implementing the IDRDataFetcher interface.

    Benefit: Enables high decoupling and makes the system easily extendable to new data sources without modifying existing code.

### ii. WebClient FactoryBean (Constraint B)

To gain granular control over the WebClient lifecycle and configuration (Base URL, filters, etc.), we implemented FactoryBean<WebClient>.
    
    Benefit: Allows the application to manage complex bean creation logic that is difficult to achieve with standard @Bean declarations, specifically for reactive external API clients.

### iii. Immutable In-Memory Store (Constraint C)

To optimize performance and meet the requirement of serving data from memory, we used a ConcurrentHashMap which is converted into an Unmodifiable Map using Map.copyOf() after the initial startup ingestion.

    Benefit: Ensures thread-safety and prevents accidental data modification during the application's runtime.

Business Logic: Spread Factor Calculation

The service calculates a unique USD Buy Spread for IDR based on the configured GitHub username:

    Unicode Sum: Sum of ASCII/Unicode values of each character in the username (lowercase).
    Spread Factor: 100,000 Sum (mod1000)
    Buy Spread IDR: (USD Rate1) × (1+Spread Factor)

Error Handling Standard

We implement a strict, unified error response format with standardized codes for easy troubleshooting in production environments.
Unified Error Schema

    {
        "errorCode": "AGG-XXX",
        "errorMessage": "Detailed description of the error",
        "timestamp": "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
        "path": "/api/finance/data/..."
    }

Error Code Reference

    Code	    Category	Description
    GNR-999	    General     Unexpected internal server error.
    AGG-001	    Not Found	Requested resource type is not available in the store.
    AGG-002	    Validation	Input fails to match the strict @Pattern or format required.

API Specification
Get Financial Data

Endpoint: GET /api/finance/data/{resourceType}
Supported Resource Types:

    latest_idr_rates
    historical_idr_usd
    supported_currencies

Success Response (200 OK):
    
    {
    "resourceType": "latest_idr_rates",
    "data": [{
            "currency": "USD",
            "rate": 0.000063,
            "USD_BuySpread_IDR": 16021.11
        }
    ]}

Testing Strategy

The project utilizes Project Reactor's StepVerifier to test reactive streams and Mockito for component isolation.

    Unit Tests: Validate the mathematical accuracy of SpreadFactorUtil.
    Strategy Tests: Ensure WebClient responses are correctly transformed into UnifiedFinanceResponse.
    Validation Tests: Verify that the GlobalExceptionHandler correctly intercepts and formats errors into the AGG-XXX standard.

Getting Started
Prerequisites

    Java 17 or higher
    Gradle 8.x

Configuration

Update src/main/resources/application.yml:
    
    api:
        frankfurter:
            base-url: https://api.frankfurter.dev/v1
    github:
        username: "your-github-username"