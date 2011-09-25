package org.indp.vdbc.ui.metadata;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import java.sql.ResultSet;
import org.indp.vdbc.ui.ResultSetTable;

/**
 *
 * @author pi
 */
public abstract class ResultSetDetailsProvider implements DetailsProvider {

    protected abstract ResultSet getResultSet() throws Exception;

    @Override
    public Component getDetailsComponent() {
        try {
            return new ResultSetTable(getResultSet());
        } catch (Exception ex) {
            return new Label(ex.getMessage());
        }
    }
}
