# AI Function Generator API

AI Function Generator API is a Java library that simplifies the integration of LLMs into your applications. It allows you to create AI-powered functions using natural language descriptions, test classes, or simulated scenarios.

## Features

- Generate functions using natural language descriptions
- Define functions through test classes
- Generate functions from simulated scenarios
- Type-safe function inputs and outputs
- Comprehensive error handling

## Prerequisites

- OpenAI API key (set as environment variable `api_key`)
- Java Development Kit (JDK) 8 or higher
- Apache Maven

## Installation

1. Clone the repository:
    ```sh
    git clone https://github.com/cmu-api-design/team1-f24
    ```

2. Set the OpenAI API key as an environment variable:
    ```sh
    api_key=your_openai_api_key
    ```

## Usage

### config.ini setup
Make a copy of the config.ini.sample file and replace with your own credentials.
Then rename the file to 'config.ini'.
Make sure the config.ini file is in the root directory.

### Compile the Project

Make sure you're in the `java-function-generator` directory:
```sh
cd java-function-generator
```

To compile the project, run:
```sh
mvn compile
```

### Generate Javadoc
To generate the Javadoc, run:
```sh
mvn javadoc:javadoc
```

## JavaScript Function Generator

A Node.js translation of the Function Generator API is available in the `javascript-function-generator` folder. It exposes the same concepts as the Java library and can be installed as an npm package.

### Install dependencies

```sh
cd javascript-function-generator
npm install
```

### Run unit tests

```sh
npm test
```
