package org.indp.vaadinace;

import com.vaadin.shared.communication.ClientRpc;

/**
*
*/
public interface AceClientRpc extends ClientRpc {

    void addCommand(String id, String bindKey);

    void setText(String text);
}
