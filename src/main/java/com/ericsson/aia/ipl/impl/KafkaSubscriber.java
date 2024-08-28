package com.ericsson.aia.ipl.impl;

import java.io.Closeable;

/**
 * Abstract interface wrapper around KafkaSubscribers
 *
 * @param <V>
 *            event type.
 */
public interface KafkaSubscriber<V> extends Runnable, Closeable {
}
