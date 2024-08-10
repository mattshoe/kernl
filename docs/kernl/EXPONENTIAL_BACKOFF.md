# `data object ExponentialBackoff`

An object representing a default implementation of the `RetryStrategy` interface that uses an exponential backoff
algorithm.

The `ExponentialBackoff` object determines the delay between retry attempts by multiplying the initial delay with a
backoff
factor for each subsequent attempt. This strategy is useful in scenarios where you want to progressively increase the
delay
between retries, reducing the load on a failing service or resource.

## Properties

### `val maxAttempts: Int`

- **Description:** The maximum number of retry attempts.
- **Value:** `3`
- **Details:** This property defines the number of times an operation should be retried before it is considered to have
  failed permanently. If the number of attempts exceeds this value, the operation will not be retried further.

### `val initialDelay: Duration`

- **Description:** The initial delay before the first retry attempt.
- **Value:** `100.milliseconds`
- **Details:** This property specifies the amount of time to wait before making the first retry attempt after a failure.
  The delay may be increased for subsequent retries based on the `backoffFactor`.

### `val backoffFactor: Double`

- **Description:** The factor by which the delay is multiplied after each retry attempt.
- **Value:** `2.0`
- **Details:** This property defines the exponential backoff factor used to increase the delay between each retry
  attempt. A value greater than 1.0 will result in increasing delays, while a value of 1.0 will keep the delay constant.

## Functions

### `fun copy(maxAttempts: Int = 3, initialDelay: Duration = 100.milliseconds, backoffFactor: Double = 2.0): RetryStrategy`

- **Description:** Creates a new instance of `RetryStrategy` using an `ExponentialBackoff` strategy with the specified
  parameters.
- **Parameters:**
    - `maxAttempts`: The maximum number of retry attempts. **Default Value:** `3`
    - `initialDelay`: The initial delay before the first retry attempt. **Default Value:** `100.milliseconds`
    - `backoffFactor`: The factor by which the delay is multiplied after each retry attempt. **Default Value:** `2.0`
- **Returns:** A new `RetryStrategy` implementation with the specified parameters.
- **Details:** This function allows you to create a customized `ExponentialBackoff` strategy while providing default
  values for each parameter. It is a convenient way to modify specific properties of the backoff strategy without
  needing to specify all parameters.

## Summary

The `ExponentialBackoff` class provides a flexible and customizable retry strategy based on the exponential backoff
algorithm. By adjusting the `maxAttempts`, `initialDelay`, and `backoffFactor` properties, you can tailor the retry
behavior to meet your application's specific needs. The class also offers a convenient `copy` function to create new
instances with customized parameters, using sensible defaults.
