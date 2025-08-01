class ErrorCondition {
  constructor(exception, predicateOrDescription) {
    if (!exception || (!predicateOrDescription && predicateOrDescription !== '' )) {
      throw new Error('All parameters must be non-null');
    }
    this.exception = exception;
    if (typeof predicateOrDescription === 'function') {
      this.predicate = predicateOrDescription;
      this.conditionDescription = null;
    } else {
      this.predicate = null;
      this.conditionDescription = predicateOrDescription;
    }
    this.errorMessage = exception.message;
  }

  validate(input) {
    if (this.predicate && this.predicate(input)) {
      throw this.exception;
    }
  }

  test(input) {
    return this.predicate ? this.predicate(input) : false;
  }

  getErrorMessage() {
    return this.errorMessage;
  }

  isNaturalLanguageCondition() {
    return this.conditionDescription !== null && this.conditionDescription !== undefined;
  }

  getConditionDescription() {
    return this.conditionDescription;
  }

  getException() {
    return this.exception;
  }

  toString() {
    return `ErrorCondition{message='${this.errorMessage}'}`;
  }
}

module.exports = ErrorCondition;
