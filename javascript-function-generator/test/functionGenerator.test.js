const { FunctionGenerator } = require('../src');
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

test('FunctionGenerator invokes strategy and parses result', async () => {
  const strategy = new MockStrategy(JSON.stringify('olleh'));
  const fn = FunctionGenerator
    .builder(String, String)
    .withDescription('Reverse input string.')
    .withStrategy(strategy)
    .build();
  const result = await fn('hello');
  expect(result).toBe('olleh');
  expect(strategy.lastPrompt).toContain('Reverse input string.');
});

test('Pre execution check throws error', async () => {
  const strategy = new MockStrategy(JSON.stringify('ok'));
  const fn = FunctionGenerator
    .builder(String, String)
    .withDescription('Echo')
    .withStrategy(strategy)
    .withPreExecutionCheck(new Error('Input cannot be null'), input => input == null)
    .build();
  await expect(fn(null)).rejects.toThrow('Input cannot be null');
});

test('Natural language error triggers exception', async () => {
  const strategy = new MockStrategy(JSON.stringify({ error: true, message: 'Too big' }));
  const fn = FunctionGenerator
    .builder(Number, Number)
    .withDescription('Square input.')
    .withStrategy(strategy)
    .withNaturalLanguageError(new RangeError('Too big'), 'Input is too large')
    .build();
  await expect(fn(5)).rejects.toThrow('Too big');
});
