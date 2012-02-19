package org.indp.vdbc.model;

import javax.sql.DataSource;
import java.io.Closeable;

/**
 *
 */
public interface DataSourceAdapter extends Closeable {

    DataSource getDataSource();

    boolean isValidProfile();
}
