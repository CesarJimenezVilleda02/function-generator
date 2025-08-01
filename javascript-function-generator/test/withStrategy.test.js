const { FunctionGenerator } = require('../src');
const MockStrategy = require('./MockStrategy');

test('withStrategy uses provided strategy and parses array output', async () => {
  const strategy = new MockStrategy(JSON.stringify([1,2,3]));
  const fn = FunctionGenerator
    .builder(Object, Array)
    .withDescription('Return numbers array')
    .withStrategy(strategy)
    .build();
  const input = { foo: { bar: ['baz'] } };
  const result = await fn(input);
  expect(Array.isArray(result)).toBe(true);
  expect(result).toEqual([1,2,3]);
  expect(strategy.lastPrompt).toContain('---INPUT---\n{"foo":{"bar":["baz"]}}\n---INPUT END---');
});

