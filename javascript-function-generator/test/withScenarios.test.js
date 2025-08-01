const { FunctionGenerator } = require('../src');
const MockStrategy = require('./MockStrategy');
const Scenario = require('../src/scenarios/Scenario');

test('withScenarios embeds scenario details and handles nested input', async () => {
  const strategy = new MockStrategy(JSON.stringify('ok'));
  const scenarios = [
    new Scenario({ deeply: { nested: 1 } }, { result: 2 }, 'nested example')
  ];
  const fn = FunctionGenerator
    .builder(Object, Object)
    .withDescription('Handle nested.')
    .withScenarios(scenarios)
    .withStrategy(strategy)
    .build();
  await fn({ input: { value: [1, { a: 2 }] } });
  expect(strategy.lastPrompt).toContain('Example Scenarios');
  expect(strategy.lastPrompt).toContain('Input: {"deeply":{"nested":1}}');
  expect(strategy.lastPrompt).toContain('---INPUT---\n{"input":{"value":[1,{"a":2}]}}\n---INPUT END---');
});
