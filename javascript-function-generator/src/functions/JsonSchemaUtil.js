function getJsonSchema(constructorFn) {
  if (constructorFn === String || constructorFn === Number || constructorFn === Boolean) {
    return constructorFn.name.toLowerCase();
  }
  try {
    const instance = new constructorFn();
    const schema = {};
    for (const key of Object.keys(instance)) {
      schema[key] = typeof instance[key];
    }
    return JSON.stringify(schema);
  } catch (e) {
    return constructorFn.name || 'unknown';
  }
}

module.exports = { getJsonSchema };
