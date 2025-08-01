const { getJsonSchema } = require('./JsonSchemaUtil');
const ErrorCondition = require('./ErrorCondition');

class FunctionGenerator {
  constructor(builder) {
    if (!builder.inputType || !builder.outputType) {
      throw new Error('Input and output types must be specified.');
    }
    if (!builder.strategy) {
      throw new Error('A function generation strategy must be provided.');
    }
    if (!builder.description) {
      throw new Error('A description must be provided.');
    }

    this.prompt = builder.scenarios.length > 0
      ? FunctionGenerator.generateScenariosPrompt(builder.description, builder.scenarios)
      : builder.description;
    this.inputType = builder.inputType;
    this.outputType = builder.outputType;
    this.client = builder.strategy;
    this.errorConditions = builder.errorConditions;
  }

  static generateScenariosPrompt(description, scenarios) {
    let prompt = `Function Description: ${description}\n\nExample Scenarios:\n`;
    scenarios.forEach((s, i) => {
      prompt += `Scenario ${i + 1}:\n${s.toString()}\n`;
    });
    prompt += '\nBased on these scenarios, process the following input:';
    return prompt;
  }

  buildPrompt(input) {
    let inputJson;
    if (typeof input === 'string') {
      inputJson = input;
    } else {
      inputJson = JSON.stringify(input);
    }

    let outputSchema;
    if (this.outputType === String) {
      outputSchema = 'string';
    } else if (this.outputType === Number) {
      outputSchema = 'number';
    } else if (this.outputType === Boolean) {
      outputSchema = 'boolean';
    } else if (typeof this.outputType === 'function') {
      outputSchema = getJsonSchema(this.outputType);
    } else {
      outputSchema = 'unknown';
    }

    let prompt = `${this.prompt} \n---INPUT---\n${inputJson}\n---INPUT END---\nYour output must conform to the following schema: ${outputSchema}.`;

    const nl = this.errorConditions.filter(e => e.isNaturalLanguageCondition());
    if (nl.length > 0) {
      prompt += '\nIMPORTANT: If any of the following conditions are met, output {"error": true, "message": "<error message>"}:\n';
      nl.forEach(c => {
        prompt += `${c.getConditionDescription()} -> ${c.getErrorMessage()}\n`;
      });
    }
    return prompt;
  }

  async invoke(input) {
    for (const condition of this.errorConditions) {
      if (!condition.isNaturalLanguageCondition()) {
        condition.validate(input);
      }
    }
    const message = this.buildPrompt(input);
    const response = await this.client.generateFunctionOutput(message);
    let parsed;
    try {
      parsed = JSON.parse(response);
    } catch (e) {
      throw new Error(`Failed to parse function output: ${e.message}`);
    }
    if (parsed && parsed.error) {
      for (const condition of this.errorConditions) {
        if (condition.isNaturalLanguageCondition() && parsed.message === condition.getErrorMessage()) {
          throw condition.getException();
        }
      }
      throw new Error(`Error invoking function: ${parsed.message}`);
    }
    return parsed;
  }

  static builder(inputType, outputType) {
    return new Builder(inputType, outputType);
  }
}

class Builder {
  constructor(inputType, outputType) {
    this.inputType = inputType;
    this.outputType = outputType;
    this.errorConditions = [];
    this.scenarios = [];
    this.description = '';
  }

  withDescription(desc) {
    this.description += desc;
    return this;
  }

  withScenarios(scenarios) {
    this.scenarios.push(...scenarios);
    return this;
  }

  withStrategy(strategy) {
    this.strategy = strategy;
    return this;
  }

  withPreExecutionCheck(exception, predicate) {
    this.errorConditions.push(new ErrorCondition(exception, predicate));
    return this;
  }

  withNaturalLanguageError(exception, description) {
    this.errorConditions.push(new ErrorCondition(exception, description));
    return this;
  }

  build() {
    const generator = new FunctionGenerator(this);
    const func = async (input) => generator.invoke(input);
    func.compose = (f) => async (input) => func(await f(input));
    func.andThen = (f) => async (input) => f(await func(input));
    return func;
  }
}

module.exports = FunctionGenerator;
