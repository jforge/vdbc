package org.indp.vdbc.ui.query;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.indp.vdbc.services.DatabaseSession;
import org.indp.vdbc.ui.ResultSetTable;
import org.indp.vdbc.util.JdbcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.aceeditor.AceEditor;
import org.vaadin.aceeditor.AceEditor.SelectionChangeEvent;
import org.vaadin.aceeditor.AceEditor.SelectionChangeListener;
import org.vaadin.aceeditor.AceMode;
import org.vaadin.aceeditor.TextRange;

import workbench.db.DbMetadata;
import workbench.sql.formatter.SqlFormatter;
import workbench.sql.formatter.SqlFormatterFactory;

import com.vaadin.data.Property;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.themes.ValoTheme;

public class QueryExecutorComponent extends VerticalLayout implements Closeable {

	private static final Logger LOG = LoggerFactory.getLogger(QueryExecutorComponent.class);
    
	private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(10);
    
    private static final boolean DEFAULT_AUTO_COMMIT = true;
    private static final String STATEMENTS_SEPARATOR = ";";
    private static final String TOOLBAR = "toolbar";

    private final Connection connection;
    //
    private VerticalSplitPanel splitPanel;
    private HorizontalLayout toolbar;
    private AceEditor editor;
    private TextRange selection;
    private TabSheet results;
    private ComboBox maxRowsBox;

    public QueryExecutorComponent(DatabaseSession databaseSession) throws SQLException {
        connection = databaseSession.getConnection();
        connection.setAutoCommit(DEFAULT_AUTO_COMMIT);

        buildQueryTextEditor();
        buildToolbar();

        results = new TabSheet();
        results.addStyleName(ValoTheme.TABSHEET_FRAMED);
        results.setSizeFull();
        VerticalLayout resultLayout = new VerticalLayout();
        resultLayout.setId("result-layout");
        resultLayout.addComponent(results);
        
        splitPanel = new VerticalSplitPanel(editor, resultLayout);
        splitPanel.setSizeFull();

        addComponents(toolbar, splitPanel);
        setSizeFull();
        setExpandRatio(splitPanel, 1);

        setCaption("Query");

        editor.focus();
    }

    @Override
    public void close() {
        JdbcUtils.close(connection);
    }

    private void buildToolbar() {
    	final Button executeButton = createToolButton("Execute", new Button.ClickListener() {
    		@Override
    		public void buttonClick(Button.ClickEvent event) {
    			executeQuery(editor.getValue());
    		}
    	});
    	executeButton.setIcon(FontAwesome.BOLT);
    	executeButton.setDescription("Press Ctrl+Enter to execute the query");

        final Button commitButton = createToolButton("Commit", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    connection.commit();
                    Notification.show("Commited");
                } catch (SQLException ex) {
                    LOG.warn("commit failed", ex);
                    Notification.show("Commit failed\n", ex.getMessage(), Notification.Type.ERROR_MESSAGE);
                }
            }
        });
        commitButton.setIcon(FontAwesome.CHECK);
        commitButton.setEnabled(false);

        final Button rollbackButton = createToolButton("Rollback", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    connection.rollback();
                    Notification.show("Rolled back");
                } catch (SQLException ex) {
                    LOG.warn("rollback failed", ex);
                    Notification.show("Rollback failed\n", ex.getMessage(), Notification.Type.ERROR_MESSAGE);
                }
            }
        });
        rollbackButton.setIcon(FontAwesome.BAN);
        rollbackButton.setEnabled(false);

        final Button formatButton = createToolButton("Format", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                String sql = editor.getValue();
                if (sql != null) {
                	SqlFormatter formatter = SqlFormatterFactory.createFormatter(DbMetadata.DBID_ORA);
                	editor.setValue(formatter.getFormattedSql(sql));
                }
            }
        });
        formatButton.setIcon(FontAwesome.CODE);
        
        final CheckBox autocommitCheckBox = new CheckBox("Autocommit", DEFAULT_AUTO_COMMIT);
        autocommitCheckBox.setImmediate(true);
        autocommitCheckBox.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                boolean autoCommit = Boolean.TRUE.equals(autocommitCheckBox.getValue());
                try {
                    connection.setAutoCommit(autoCommit);
                    commitButton.setEnabled(!autoCommit);
                    rollbackButton.setEnabled(!autoCommit);
                } catch (Exception ex) {
                    autocommitCheckBox.setValue(!autoCommit);
                    Notification.show("Failed to change autocommit setting\n", ex.getMessage(), Notification.Type.ERROR_MESSAGE);
                }
            }
        });

        maxRowsBox = new ComboBox(null, Arrays.asList(10, 100, 500, 1000));
        maxRowsBox.addStyleName(ValoTheme.COMBOBOX_SMALL);
        maxRowsBox.setDescription("Max number of rows to retrieve");
        maxRowsBox.setWidth("100px");
        maxRowsBox.setNewItemsAllowed(false);
        maxRowsBox.setNullSelectionAllowed(false);
        maxRowsBox.setTextInputAllowed(false);
        maxRowsBox.setValue(100);

        CssLayout commitRollbackGroup = new CssLayout(commitButton, rollbackButton);
        commitRollbackGroup.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

        toolbar = new HorizontalLayout();
        toolbar.setId(TOOLBAR);
        toolbar.addStyleName(TOOLBAR);
        toolbar.setSpacing(true);
        toolbar.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        toolbar.addComponents(
                executeButton,
                formatButton,
                commitRollbackGroup,
                autocommitCheckBox,
                maxRowsBox);
        toolbar.setWidth("100%");
        toolbar.setExpandRatio(autocommitCheckBox, 1);
        toolbar.setComponentAlignment(maxRowsBox, Alignment.MIDDLE_RIGHT);
    }

    @SuppressWarnings("serial")
	private void buildQueryTextEditor() {
		editor = new AceEditor();
		editor.setMode(AceMode.sql);
		editor.setFontSize("12px");
		editor.setShowPrintMargin(false);
		editor.setSizeFull();
		editor.setTextChangeEventMode(TextChangeEventMode.LAZY);
		editor.addSelectionChangeListener(new SelectionChangeListener() {
		    @Override
		    public void selectionChanged(SelectionChangeEvent e) {
		    	selection = e.getSelection();
		    }
		});
//        query.setStyleName("monospace");
        editor.addShortcutListener(new ShortcutListener("Run Query", null, ShortcutAction.KeyCode.ENTER, ShortcutAction.ModifierKey.CTRL) {
            @Override
            public void handleAction(Object sender, Object target) {
            	String value = editor.getValue();
		        int cursorPosition = selection.getCursorPosition();
		        if (cursorPosition > 0 && value != null) {
		        	String lastChar = value.substring(cursorPosition - 1, cursorPosition);
		        	if (STATEMENTS_SEPARATOR.equals(lastChar)) {
		        		String area = value.substring(0, cursorPosition - 1);
		        		int rangeStart = area.indexOf(STATEMENTS_SEPARATOR);
		        		value = area.substring(rangeStart + 1);
		        	}
		        }            	
                executeQuery(value);
            }
        });
    }

    protected Button createToolButton(String caption, Button.ClickListener clickListener) {
        final Button button = new Button(caption, clickListener);
        button.addStyleName(ValoTheme.BUTTON_SMALL);
        return button;
    }

    protected void executeQuery(String sql) {
        if (sql == null || sql.isEmpty()) {
            return;
        }
        String statement = sql;
        if (selection != null && !selection.isZeroLength()) {
        	int start = selection.getStart();
        	int end = selection.getEnd();
        	if (start > end) {
        		statement = statement.substring(end, start);
        	} else {
        		statement = statement.substring(start, end);
        	}        	
        	
        }
        // TODO cancelable execution
        final ProgressBar progressBar = new ProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setSizeFull();
        
        setExecutionAllowed(false);
    	
        Tab rt = results.getTab(results.getSelectedTab());
        if (rt != null) {
        	ResultBox resultBox = (ResultBox) rt.getComponent();
        	if (resultBox.isInError()) {
        		rt.setIcon(FontAwesome.PLAY);
        		resultBox.removeAllComponents();
        		resultBox.addComponent(progressBar);
        		resultBox.setException(null);
        	} else {
        		rt = null;
        	}
        }
        if (rt == null) {
        	int resultsNum = results.getComponentCount();
            final ResultBox rb = new ResultBox();
            rb.setSizeFull();
            rb.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
            rb.addComponent(progressBar);
        	rt = results.addTab(rb, "Query Result " + (resultsNum > 0 ? resultsNum : ""), FontAwesome.PLAY);
        }
        final Tab currentTab = rt;
        rt.setClosable(true);
        results.setSelectedTab(rt);
        EXECUTOR.submit(new QueryTask(currentTab, statement));
    }

    private void handleQueryExecution(Tab resultTab, String sql) {
        String statMsg;
        final long start = System.currentTimeMillis();
        try {
            try (PreparedStatement stmt = connection.prepareStatement(sql.replaceAll(STATEMENTS_SEPARATOR, ""))) {
                stmt.setMaxRows(getMaxRows());
                boolean hasResultSet = stmt.execute();
                if (hasResultSet) {
                    ResultSetTable table = new ResultSetTable(stmt.getResultSet());
                    statMsg = "rows fetched: " + table.getItemIds().size();
                    showResult(resultTab, table);
                } else {
                    int cnt = stmt.getUpdateCount();
                    statMsg = "rows updated: " + cnt;
                    showResult(resultTab, new Label("Updated " + cnt + " row(s)"));
                }
            }

            final long end = System.currentTimeMillis();
            final String finalStatMsg = statMsg;
            getUI().access(new Runnable() {
                @Override
                public void run() {
                    Notification.show(
                            "Query Stats",
                            "exec time: " + (end - start) / 1000.0 + " ms\n" + finalStatMsg,
                            Notification.Type.TRAY_NOTIFICATION);
                }
            });
        } catch (SQLException e) {
            LOG.debug("failed to execute sql query", e);
            resultTab.setIcon(FontAwesome.EXCLAMATION_CIRCLE);
            ((ResultBox) resultTab.getComponent()).setException(e);
            showResult(resultTab, new Label(e.getMessage()));
        }
    }

    private void showResult(final Tab resultTab, final Component resultComponent) {
        getUI().access(new Runnable() {
            @Override
            public void run() {
            	ResultBox resultBox = (ResultBox) resultTab.getComponent();
            	resultBox.removeAllComponents();
            	resultBox.addComponent(resultComponent);
                editor.focus();
                setExecutionAllowed(true);
            }
        });
    }

    private int getMaxRows() {
        Object value = maxRowsBox.getValue();
        return value == null ? 0 : (Integer) value;
    }

    private void setExecutionAllowed(boolean b) {
        editor.setReadOnly(!b);
        toolbar.setEnabled(b);
    }
    
    private class QueryTask implements Runnable {

    	private Tab tab;
    	
    	private String query;
    	
    	public QueryTask(Tab tab, String query) {
			this.query = query;
		}
    	
		@Override
		public void run() {
			handleQueryExecution(tab, query);
		}
    	
    }
    
    private static class ResultBox extends VerticalLayout {
    	
    	private Exception exception;

		public Exception getException() {
			return exception;
		}

		public void setException(Exception exception) {
			this.exception = exception;
		}
    
		public boolean isInError() {
			return exception != null;
		}
		
    }
    
    
}
