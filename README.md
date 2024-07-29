# GitHub Repository Fetcher

This Spring Boot application fetches all non-forked repositories for a given GitHub username and retrieves the branches and their last commit SHA for each repository. It uses WebClient for making API calls to the GitHub REST API.

## Features

- Fetch non-forked repositories for a specified GitHub user.
- Retrieve branches and their last commit SHA for each repository.
- Handles errors gracefully with appropriate logging.
- Ensures JSON response format using Spring's content negotiation.

## Requirements

- Java 21 or later
- Gradle 8 or later
- GitHub personal access token (for accessing GitHub API if needed)

## Setup

1. **Clone the Repository**

   ```bash
   git clone https://github.com/bkalika/atipera.git
   cd atipera

## API Documentation
This application includes automatically generated API documentation and a Swagger UI to interact with the API endpoints.
- API Docs: View the full API documentation at [ApiDocs](http://localhost:8080/api-docs)
- Swagger UI: Explore and test the API using the Swagger UI at [Swagger](http://localhost:8080/swagger-ui/index.html)

## Contact
For questions or suggestions, please open an issue or contact [bogdan.kalika@gmail.com](mailto:bogdan.kalika@gmail.com).
