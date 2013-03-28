package org.indp.vdbc.ui.workspace;

/**
 *
 */
public interface WorkspaceModule {

    String getTitle();

    ModuleContentComponent createDefaultView();
}
