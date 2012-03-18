package org.indp.vdbc.services;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 */
public interface ConnectionAwareAction {

    void execute(Connection connection) throws SQLException;

}
