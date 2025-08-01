# JavaScript Function Generator

The JavaScript Function Generator mirrors the Java API for creating AI-powered functions from natural language descriptions and scenarios. It targets Node.js environments.

## Installation

```sh
npm install javascript-function-generator
```

## Usage

```js
const { FunctionGenerator, OpenAIFunctionGenerator } = require('javascript-function-generator');

const strategy = OpenAIFunctionGenerator.builder()
  .withApiKey('your-api-key')
  .build();

const fn = FunctionGenerator.builder(String, String)
  .withDescription('Reverse the input string')
  .withStrategy(strategy)
  .build();

const result = await fn('hello');
```

## Testing

Run unit tests with:

```sh
npm test
```
