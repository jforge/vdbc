package org.indp.vdbc;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;

@WebServlet(name = "vdbc", urlPatterns = "/*", asyncSupported = true)
@VaadinServletConfiguration(productionMode = true, ui = VdbcUI.class)
public class VdbcServlet extends VaadinServlet {
}
