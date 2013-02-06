package org.indp.vaadinace;

import com.vaadin.annotations.JavaScript;
import com.vaadin.shared.ui.JavaScriptComponentState;
import com.vaadin.ui.AbstractJavaScriptComponent;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 *
 */
@JavaScript({"js/ace.js", "js/mode-sql.js", "js/theme-textmate.js", "ace-connector.js"})
public class SourceCodeField extends AbstractJavaScriptComponent {
    private static final Logger log = LoggerFactory.getLogger(SourceCodeField.class);
    private Map<String, Runnable> commands = new HashMap<String, Runnable>();
    private String text;

    public SourceCodeField() {
        registerRpc(new AceServerRpc() {
            @Override
            public void executeCommand(String id, JSONObject stateObject) {
                log.debug("executeCommand");
                try {
                    setText(stateObject.getString("text"), false);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                SourceCodeField.this.executeCommand(id);
            }

// todo uncomment after they fix http://dev.vaadin.com/ticket/10946
//            @Override
//            public void editorValueChanged(String newValue) {
//                log.debug("editorValueChanged");
//                text = newValue;
//            }
        });
    }

    public String registerCommand(String bindKey, Runnable runnable) {
        String id = UUID.randomUUID().toString();
        getRpcProxy(AceClientRpc.class).addCommand(id, bindKey);
        commands.put(id, runnable);
        return id;
    }

    private void executeCommand(String commandId) {
        Runnable runnable = commands.get(commandId);
        if (runnable == null) {
            throw new IllegalArgumentException("Unknown command: " + commandId);
        }
        runnable.run();
    }

    @Override
    protected State getState() {
        return (State) super.getState();
    }

    @Override
    protected State createState() {
        return new State();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        setText(text, true);
    }

    private void setText(String text, boolean sendToClient) {
        this.text = text;
        if (sendToClient) {
            getRpcProxy(AceClientRpc.class).setText(text);
        }
    }

    public static final class State extends JavaScriptComponentState {
        private String language = "sql";

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }
    }
}
