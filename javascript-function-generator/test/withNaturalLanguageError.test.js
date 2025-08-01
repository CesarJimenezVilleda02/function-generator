const { FunctionGenerator } = require('../src');
const MockStrategy = require('./MockStrategy');

test('withNaturalLanguageError reacts to LLM error response', async () => {
  const errorMsg = 'Nested value too big';
  const strategy = new MockStrategy(JSON.stringify({ error: true, message: errorMsg }));
  const fn = FunctionGenerator
    .builder(Object, Object)
    .withDescription('desc')
    .withStrategy(strategy)
    .withNaturalLanguageError(new RangeError(errorMsg), 'value exceeds limit')
    .build();
  await expect(fn({ val: 1000 })).rejects.toThrow(errorMsg);
  expect(strategy.lastPrompt).toContain('value exceeds limit');
});
