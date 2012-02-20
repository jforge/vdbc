package org.indp.vdbc.model;

import org.indp.vdbc.exceptions.InvalidProfileException;

import javax.sql.DataSource;
import java.io.Closeable;

/**
 *
 */
public interface DataSourceAdapter extends Closeable {

    DataSource getDataSource();

    void validateProfile() throws InvalidProfileException;
}
