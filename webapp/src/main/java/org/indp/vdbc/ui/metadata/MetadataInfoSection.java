package org.indp.vdbc.ui.metadata;

import com.vaadin.ui.Button;
import com.vaadin.ui.themes.BaseTheme;

/**
 *
 *
 */
public class MetadataInfoSection extends Button {

    public MetadataInfoSection(String title, final DatabaseMetadataView.BrowserCallback browserCallback, final DetailsProvider detailsProvider) {
        setCaption(title);
        setStyleName(BaseTheme.BUTTON_LINK);
        addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                browserCallback.showDetails(detailsProvider.getDetailsComponent());
            }
        });
    }
}
