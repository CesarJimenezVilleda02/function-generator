const FunctionGenerationStrategy = require('../src/strategies/FunctionGenerationStrategy');

class MockStrategy extends FunctionGenerationStrategy {
  constructor(response) {
    super();
    this.response = response;
    this.lastPrompt = null;
  }
  async generateFunctionOutput(prompt) {
    this.lastPrompt = prompt;
    return this.response;
  }
}

module.exports = MockStrategy;
