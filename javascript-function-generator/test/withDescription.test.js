const { FunctionGenerator } = require('../src');
const MockStrategy = require('./MockStrategy');

test('withDescription appends text to prompt', async () => {
  const strategy = new MockStrategy(JSON.stringify('ok'));
  const fn = FunctionGenerator
    .builder(String, String)
    .withDescription('First part. ')
    .withDescription('Second part.')
    .withStrategy(strategy)
    .build();
  await fn('hello');
  expect(strategy.lastPrompt).toContain('First part. Second part.');
});
