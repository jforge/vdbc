package org.indp.vdbc.ui.metadata;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.themes.BaseTheme;

/**
 *
 *
 */
public class MetadataInfoSection extends CustomComponent {

    public MetadataInfoSection(String title, final DatabaseMetadataView.BrowserCallback browserCallback, final DetailsProvider detailsProvider) {
        Button button = new Button(title, new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                browserCallback.showDetails(detailsProvider.getDetailsComponent());
            }
        });
        button.setStyleName(BaseTheme.BUTTON_LINK);
        setCompositionRoot(button);
    }
}
