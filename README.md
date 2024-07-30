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

## Docker Setup

This project can be containerized using Docker. Follow the instructions below to build and run the Docker image for this project.

### Prerequisites

- [Docker](https://docs.docker.com/get-docker/) installed on your machine.
- Basic knowledge of Docker commands.

### Building the Docker Image

1. **Ensure the application is built**: Before building the Docker image, make sure your application is built and the JAR file is available. Run the following command:

   ```bash
   ./gradlew clean build

2. **Build the Docker image:** Use the following command to build the Docker image:

   ```bash
   docker build -t gr .

3. **Run the Docker container:** Start a container from the built image using the following command. This command also sets the GIT_HUB_TOKEN environment variable.

   ```bash
   docker run -p 8080:8080 -e GIT_HUB_TOKEN={YOUR_GITHUB_TOKEN} gr

## API Documentation
This application includes automatically generated API documentation and a Swagger UI to interact with the API endpoints.
- API Docs: View the full API documentation at [ApiDocs](http://localhost:8080/api-docs)
- Swagger UI: Explore and test the API using the Swagger UI at [Swagger](http://localhost:8080/swagger-ui/index.html)

## Errors

When an error occurs, the application will return a response with appropriate HTTP Status code and message with the following structure:

```
{
  "status": "${httpStatusCode}",
  "message": "${errorMessage}"
}
```

## Contact
For questions or suggestions, please open an issue or contact [bogdan.kalika@gmail.com](mailto:bogdan.kalika@gmail.com).
