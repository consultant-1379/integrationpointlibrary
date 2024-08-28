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
package com.ericsson.aia.ipl.batch.util;

import com.ericsson.aia.ipl.impl.KafkaBatchSubscriberWrapper;

/**
 * constants used as keys in {@link KafkaBatchSubscriberWrapper} attributes when creating batch events
 *
 */
public final class BatchConstants {

    public static final String COMMITTER = "committer";

    public static final String TOPIC = "topic";

    public static final String OFFSETS = "offsets";

    public static final String NUMBER_OF_PARTITIONS = "numberOfPartitions";

    public static final String ASSIGNED_PARTITIONS = "assignedPartitions";

    private BatchConstants() {
    }
}
