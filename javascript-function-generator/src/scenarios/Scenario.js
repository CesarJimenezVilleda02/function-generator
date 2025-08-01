class Scenario {
  constructor(input, output, description) {
    this.input = input;
    this.output = output;
    this.description = description;
  }

  toString() {
    return `Input: ${JSON.stringify(this.input)}\nExpected Output: ${JSON.stringify(this.output)}\nDescription: ${this.description}`;
  }
}

module.exports = Scenario;
