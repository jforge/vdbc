package org.indp.vdbc;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;

import javax.servlet.annotation.WebServlet;

/**
 *
 */
@WebServlet(name = "app", urlPatterns = "/*", asyncSupported = true)
@VaadinServletConfiguration(productionMode = true, ui = VdbcUI.class)
public class VdbcServlet extends VaadinServlet {
}
