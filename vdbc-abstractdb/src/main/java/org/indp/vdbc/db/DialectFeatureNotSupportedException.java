package org.indp.vdbc.db;

/**
 *
 */
public class DialectFeatureNotSupportedException extends RuntimeException {
    public DialectFeatureNotSupportedException(String featureId) {
        super("Feature not supported: " + featureId);
    }
}
