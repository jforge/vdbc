package org.indp.vdbc.ui.explorer;

import com.vaadin.ui.Component;

/**
 * The state of the details component.
 */
public interface ObjectDetails extends Component {

    DetailsState getDetailsState();

    void setDetailsState(DetailsState state);

    boolean isTemporary();
}
