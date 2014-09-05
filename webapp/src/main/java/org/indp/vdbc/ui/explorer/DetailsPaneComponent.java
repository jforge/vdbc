package org.indp.vdbc.ui.explorer;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.themes.ValoTheme;

public class DetailsPaneComponent extends CustomComponent {

    private TabSheet tabSheet;

    public DetailsPaneComponent() {
        setCompositionRoot(new Label());
        setSizeFull();
    }

    public void showDetails(ObjectDetails objectDetails) {
        if (tabSheet == null) {
            tabSheet = new TabSheet();
            tabSheet.setSizeFull();
            tabSheet.addStyleName(ValoTheme.TABSHEET_FRAMED);
            setCompositionRoot(tabSheet);
        }

        // first we try to replace selected tab
        ObjectDetails selectedTab = getSelectedDetailComponent();
        if (selectedTab != null && selectedTab.isTemporary()) {
            replaceDetailsTab(selectedTab, objectDetails);
            return;
        }

        // now let's try to find the first temporary tab
        for (int i = 0; i < tabSheet.getComponentCount(); i++) {
            ObjectDetails component = (ObjectDetails) tabSheet.getTab(i).getComponent();
            if (component.isTemporary()) {
                replaceDetailsTab(component, objectDetails);
                return;
            }
        }

        // no temporary tabs, so we create one
        tabSheet.addTab(objectDetails);
        setupTab(objectDetails);
    }

    public ObjectDetails getSelectedDetailComponent() {
        return tabSheet == null ? null : (ObjectDetails) tabSheet.getSelectedTab();
    }

    private void replaceDetailsTab(ObjectDetails oldContent, ObjectDetails newContent) {
        tabSheet.replaceComponent(oldContent, newContent);
        setupTab(newContent);
    }

    private void setupTab(ObjectDetails component) {
        TabSheet.Tab tab = tabSheet.getTab(component);
        tab.setCaption(component.getCaption());
        tab.setIcon(component.getIcon());
        tab.setClosable(true);
        tabSheet.setSelectedTab(component);
    }
}
