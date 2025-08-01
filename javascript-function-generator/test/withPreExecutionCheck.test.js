const { FunctionGenerator } = require('../src');
const MockStrategy = require('./MockStrategy');

test('withPreExecutionCheck validates nested object', async () => {
  const strategy = new MockStrategy(JSON.stringify('ok'));
  const fn = FunctionGenerator
    .builder(Object, String)
    .withDescription('Check nested')
    .withStrategy(strategy)
    .withPreExecutionCheck(new Error('Missing inner value'), input => !input.inner || input.inner.value == null)
    .build();
  await expect(fn({ inner: {} })).rejects.toThrow('Missing inner value');
});
