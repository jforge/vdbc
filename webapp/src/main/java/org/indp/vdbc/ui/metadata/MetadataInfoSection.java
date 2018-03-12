package org.indp.vdbc.ui.metadata;

import com.vaadin.ui.Button;
import com.vaadin.v7.ui.themes.BaseTheme;

/**
 *
 *
 */
public class MetadataInfoSection extends Button {

    public MetadataInfoSection(String title, final DatabaseMetadataView.BrowserCallback browserCallback, final DetailsProvider detailsProvider) {
        setCaption(title);
        setStyleName(BaseTheme.BUTTON_LINK);
        addClickListener((ClickEvent event) -> {
            browserCallback.showDetails(detailsProvider.getDetailsComponent());
        });
    }
}
