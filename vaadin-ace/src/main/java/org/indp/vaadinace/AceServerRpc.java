package org.indp.vaadinace;

import com.vaadin.shared.communication.ServerRpc;
import org.json.JSONObject;

/**
 *
 */
public interface AceServerRpc extends ServerRpc {

    void executeCommand(String id, JSONObject state);

// todo uncomment after they fix http://dev.vaadin.com/ticket/10946
//    @Delayed(lastOnly = true)
//    void editorValueChanged(String newValue);
}
