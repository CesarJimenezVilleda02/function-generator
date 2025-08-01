const { FunctionGenerator } = require('../src');
const FunctionGenerationStrategy = require('../src/strategies/FunctionGenerationStrategy');
const Scenario = require('../src/scenarios/Scenario');

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

test('Prompt includes JSON for object input', async () => {
  const strategy = new MockStrategy(JSON.stringify('ok'));
  const fn = FunctionGenerator
    .builder(Object, String)
    .withDescription('Process object input.')
    .withStrategy(strategy)
    .build();
  await fn({ foo: 'bar' });
  expect(strategy.lastPrompt).toContain('---INPUT---\n{"foo":"bar"}\n---INPUT END---');
});

test('Prompt includes JSON for array input', async () => {
  const strategy = new MockStrategy(JSON.stringify('ok'));
  const fn = FunctionGenerator
    .builder(Array, String)
    .withDescription('Process array input.')
    .withStrategy(strategy)
    .build();
  await fn([1, 2, 3]);
  expect(strategy.lastPrompt).toContain('---INPUT---\n[1,2,3]\n---INPUT END---');
});

test('Prompt includes JSON for array of objects input', async () => {
  const strategy = new MockStrategy(JSON.stringify('ok'));
  const fn = FunctionGenerator
    .builder(Array, String)
    .withDescription('Process array of objects.')
    .withStrategy(strategy)
    .build();
  await fn([{ a: 1 }, { b: 2 }]);
  expect(strategy.lastPrompt).toContain('---INPUT---\n[{"a":1},{"b":2}]\n---INPUT END---');
});

test('Scenario prompt includes example details', async () => {
  const strategy = new MockStrategy(JSON.stringify('ok'));
  const scenarios = [
    new Scenario({ a: 1 }, { b: 2 }, 'maps a to b'),
    new Scenario({ c: 3 }, { d: 4 }, 'maps c to d')
  ];
  const fn = FunctionGenerator
    .builder(Object, Object)
    .withDescription('Map keys to new keys.')
    .withScenarios(scenarios)
    .withStrategy(strategy)
    .build();
  await fn({ a: 1 });
  expect(strategy.lastPrompt).toContain('Example Scenarios:');
  expect(strategy.lastPrompt).toContain('Scenario 1:');
  expect(strategy.lastPrompt).toContain('Input: {"a":1}');
  expect(strategy.lastPrompt).toContain('Expected Output: {"b":2}');
  expect(strategy.lastPrompt).toContain('Description: maps a to b');
});
