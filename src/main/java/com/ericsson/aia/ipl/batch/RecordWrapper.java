/*
 * ------------------------------------------------------------------------------
 *  *******************************************************************************
 *  * COPYRIGHT Ericsson 2017
 *  *
 *  * The copyright to the computer program(s) herein is the property of
 *  * Ericsson Inc. The programs may be used and/or copied only with written
 *  * permission from Ericsson Inc. or in accordance with the terms and
 *  * conditions stipulated in the agreement/contract under which the
 *  * program(s) have been supplied.
 *  *******************************************************************************
 *  *----------------------------------------------------------------------------
 */

package com.ericsson.aia.ipl.batch;

/**
 * Wraps records that are sent from the subscriber to avoid exposing implementation details
 * @param <V> The data type coming from the subscriber
 */
public class RecordWrapper<V> {

    private final V record;
    private final String key;
    private final String source;

    /**
     * Instantiate an event wrapper with a consumer record
     * @param record
     *              - The data taken from the source
     * @param key
     *              - Record key
     * @param source
     *              - Where the data came from in case there are multiple input sources
     */
    public RecordWrapper(final V record, final String key, final String source) {
        this.record = record;
        this.key = key;
        this.source = source;
    }

    public V getRecord() {
        return record;
    }

    public String getKey() {
        return key;
    }

    public String getSource() {
        return source;
    }
}
