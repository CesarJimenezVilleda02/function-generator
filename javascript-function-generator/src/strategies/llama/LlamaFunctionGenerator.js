const FunctionGenerationStrategy = require('../FunctionGenerationStrategy');

const API_ENDPOINT = 'https://api.llama-api.com/chat/completions';

class LlamaFunctionGenerator extends FunctionGenerationStrategy {
  constructor(builder) {
    super();
    this.apiKey = builder.apiKey;
    this.model = builder.model || 'llama3.1-70b';
    this.temperature = builder.temperature;
    this.maxTokens = builder.maxTokens;
    this.topP = builder.topP;
    this.timeout = builder.timeout;
  }

  async generateFunctionOutput(description) {
    return this.sendRequest(this.model, description);
  }

  async sendRequest(model, message) {
    const body = {
      model,
      messages: [{ role: 'user', content: message }]
    };
    if (this.temperature !== undefined) body.temperature = this.temperature;
    if (this.maxTokens !== undefined) body.max_tokens = this.maxTokens;
    if (this.topP !== undefined) body.top_p = this.topP;

    const controller = this.timeout ? new AbortController() : null;
    if (controller) {
      setTimeout(() => controller.abort(), this.timeout * 1000);
    }

    const response = await fetch(API_ENDPOINT, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${this.apiKey}`
      },
      body: JSON.stringify(body),
      signal: controller ? controller.signal : undefined
    });

    if (!response.ok) {
      const text = await response.text();
      throw new Error(`Llama API error (${response.status}): ${text}`);
    }

    const json = await response.json();
    if (!json.choices || json.choices.length === 0) {
      throw new Error('Llama API error: Response contained no choices');
    }
    return json.choices[0].message.content;
  }

  static builder() {
    return new Builder();
  }
}

class Builder {
  withApiKey(apiKey) {
    if (!apiKey) throw new Error('API key cannot be null or empty');
    this.apiKey = apiKey;
    return this;
  }

  withModel(model) {
    this.model = model;
    return this;
  }

  withTemperature(temp) {
    this.temperature = temp;
    return this;
  }

  withMaxTokens(maxTokens) {
    this.maxTokens = maxTokens;
    return this;
  }

  withTopP(topP) {
    this.topP = topP;
    return this;
  }

  withTimeout(timeout) {
    this.timeout = timeout;
    return this;
  }

  build() {
    if (!this.apiKey) throw new Error('API key is required');
    return new LlamaFunctionGenerator(this);
  }
}

module.exports = LlamaFunctionGenerator;
