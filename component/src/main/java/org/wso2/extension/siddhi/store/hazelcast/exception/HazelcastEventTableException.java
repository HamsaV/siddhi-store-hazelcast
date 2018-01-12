package org.wso2.extension.siddhi.store.hazelcast.exception;


import org.wso2.siddhi.core.exception.SiddhiAppCreationException;

/**
 * Hazelcast Event table exception
 */
public class HazelcastEventTableException extends SiddhiAppCreationException {
    public HazelcastEventTableException(String message) {
        super(message);
    }

    public HazelcastEventTableException(String message, Throwable throwable) {
        super(message, throwable);
    }


}
