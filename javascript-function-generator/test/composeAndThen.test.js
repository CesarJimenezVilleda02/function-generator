const { FunctionGenerator } = require('../src');
const MockStrategy = require('./MockStrategy');

test('compose preprocesses input before invocation', async () => {
  const strategy = new MockStrategy(JSON.stringify('olleh'));
  const fn = FunctionGenerator
    .builder(String, String)
    .withDescription('Reverse')
    .withStrategy(strategy)
    .build()
    .compose(async s => s.split('').reverse().join(''));
  const result = await fn('hello');
  expect(result).toBe('olleh');
});

test('andThen postprocesses the output', async () => {
  const strategy = new MockStrategy(JSON.stringify({ result: 2 }));
  const base = FunctionGenerator
    .builder(Number, Object)
    .withDescription('Square')
    .withStrategy(strategy)
    .build();
  const fn = base.andThen(async out => out.result);
  const result = await fn(1);
  expect(result).toBe(2);
});
