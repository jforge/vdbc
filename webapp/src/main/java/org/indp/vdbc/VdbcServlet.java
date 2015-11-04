package org.indp.vdbc;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;

// @formatter:off
@WebServlet(name = "vdbc", urlPatterns = "/*", asyncSupported = true, initParams = {
	@WebInitParam(name = "widgetset", value = "org.indp.vdbc.VdbcWidgetSet")
})
// @formatter:on
@VaadinServletConfiguration(productionMode = true, ui = VdbcUI.class)
public class VdbcServlet extends VaadinServlet {
}
