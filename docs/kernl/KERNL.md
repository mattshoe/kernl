# `object Kernl`

The `kernl` dsl manages global configurations for the `Kernl` framework

## Properties

### `events`
- **Type:** [`Flow<KernlEvent>`](KERNL_EVENT.md)
- **Description:** A flow of cache-related events that can be observed.

## Functions

### `suspend fun globalEvent(event: KernlEvent)`
- **Description:** Emits a global cache-related event.
- **Parameters:**
    - `event`: The [`KernlEvent`](KERNL_EVENT.md) to be emitted.

## Summary

The `Kernl` object provides a centralized mechanism for managing and emitting global cache-related events. The `events`
flow can be observed to react to these events, and the `globalEvent` function can be used to emit new events.
