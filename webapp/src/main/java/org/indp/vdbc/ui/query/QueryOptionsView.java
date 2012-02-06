package org.indp.vdbc.ui.query;

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;
import org.indp.vdbc.ui.ActionListenerAdapter;
import org.indp.vdbc.ui.UiUtils;

import java.awt.event.ActionListener;
import java.util.Arrays;

/**
 *
 *
 */
public class QueryOptionsView extends HorizontalLayout {

    //    private ActionListener formatSqlAction;
    private ActionListenerAdapter executeActionListener = new ActionListenerAdapter();
    private ActionListenerAdapter commitActionListener = new ActionListenerAdapter();
    private ActionListenerAdapter rollbackActionListener = new ActionListenerAdapter();
    private AutocommitSettingListener autocommitSettingListener;
    private ComboBox maxRowsBox;

    public static interface AutocommitSettingListener {

        void setAutoCommit(boolean enabled) throws Exception;
    }

    public QueryOptionsView() {
//        setSpacing(true);
        setWidth("100%");

//        final Button formatSqlButton = new Button("Format", new Button.ClickListener() {
//
//            @Override
//            public void buttonClick(ClickEvent event) {
//                if (null != formatSqlAction)
//                    formatSqlAction.actionPerformed(null);
//            }
//        });
//        formatSqlButton.addStyleName("small");

        final Button executeButton = createToolButton("Execute", executeActionListener);
        executeButton.setDescription("Press Ctrl+Enter to execute the query");

        final Button commitButton = createToolButton("Commit", commitActionListener);
        commitButton.setEnabled(false);
        final Button rollbackButton = createToolButton("Rollback", rollbackActionListener);
        rollbackButton.setEnabled(false);

        final CheckBox autocommitCheckBox = new CheckBox("Autocommit", true);
        autocommitCheckBox.setImmediate(true);
        autocommitCheckBox.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                boolean e = !autocommitCheckBox.getValue().equals(Boolean.TRUE);
                try {
                    autocommitSettingListener.setAutoCommit(!e);
                    commitButton.setEnabled(e);
                    rollbackButton.setEnabled(e);
                } catch (Exception ex) {
                    autocommitCheckBox.setValue(!e);
                    getApplication().getMainWindow().showNotification("Failed to change autocommit setting<br/>", ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
                }
            }
        });

//        Label maxRowsLabel = new Label("Max rows:");
        maxRowsBox = new ComboBox(null, Arrays.asList(10, 100, 500, 1000));
        maxRowsBox.setDescription("Max number of rows to retrieve");
        maxRowsBox.setWidth(100, UNITS_PIXELS);
        maxRowsBox.setNewItemsAllowed(false);
//        maxRowsBox.setNewItemHandler(new NewItemHandler() {
//
//            @Override
//            public void addNewItem(String newItemCaption) {
//                try {
//                    Integer i = Integer.valueOf(newItemCaption);
//                    maxRowsBox.addItem(i);
//                    maxRowsBox.setValue(i);
//                } catch (Exception ex) {
//                }
//            }
//        });
        maxRowsBox.setNullSelectionAllowed(false);
//        maxRowsBox.setNullSelectionItemId(100);
        maxRowsBox.setValue(100);


//        addComponent(formatSqlButton);
//        addComponent(UiUtils.createHorizontalSpacer(15));
        addComponent(executeButton);
        addComponent(UiUtils.createHorizontalSpacer(5));
        addComponent(commitButton);
        addComponent(rollbackButton);
        addComponent(UiUtils.createHorizontalSpacer(5));
        addComponent(autocommitCheckBox);
        addComponent(maxRowsBox);
        setExpandRatio(autocommitCheckBox, 1);
        setComponentAlignment(executeButton, Alignment.MIDDLE_LEFT);
        setComponentAlignment(commitButton, Alignment.MIDDLE_LEFT);
        setComponentAlignment(rollbackButton, Alignment.MIDDLE_LEFT);
        setComponentAlignment(autocommitCheckBox, Alignment.MIDDLE_LEFT);
        setComponentAlignment(maxRowsBox, Alignment.MIDDLE_RIGHT);
    }

    protected Button createToolButton(String caption, Button.ClickListener clickListener) {
        final Button button = new Button(caption, clickListener);
        button.addStyleName("small");
        return button;
    }

    public void setExecuteActionListener(ActionListener executeActionListener) {
        this.executeActionListener.setActionListener(executeActionListener);
    }

    public void setCommitActionListener(ActionListener commitActionListener) {
        this.commitActionListener.setActionListener(commitActionListener);
    }

    public void setRollbackActionListener(ActionListener rollbackActionListener) {
        this.rollbackActionListener.setActionListener(rollbackActionListener);
    }
//    public void setFormatSqlAction(ActionListener formatSqlAction) {
//        this.formatSqlAction = formatSqlAction;
//    }

    public int getMaxRows() {
        return (Integer) maxRowsBox.getValue();
    }

    public void setAutocommitSettingListener(AutocommitSettingListener autocommitSettingListener) {
        this.autocommitSettingListener = autocommitSettingListener;
    }
}
