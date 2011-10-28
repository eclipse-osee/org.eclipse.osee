/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.vaadin.internal;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.Date;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.vaadin.ApplicationFactory;
import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.AbstractApplicationServlet;
import com.vaadin.terminal.gwt.server.JsonPaintTarget;
import com.vaadin.terminal.gwt.server.SystemMessageException;
import com.vaadin.ui.Window;

/**
 * @author Roberto E. Escobar
 */
@SuppressWarnings("serial")
public class ApplicationServlet extends AbstractApplicationServlet {

   private final ApplicationFactory factory;
   private final Set<ApplicationSession> sessions;
   private final Log logger;

   public ApplicationServlet(Log logger, Set<ApplicationSession> sessions, ApplicationFactory factory) {
      this.logger = logger;
      this.sessions = sessions;
      this.factory = factory;
   }

   private Log getLogger() {
      return logger;
   }

   @Override
   protected Application getNewApplication(HttpServletRequest request) throws ServletException {
      Application application = factory.createInstance();
      if (application == null) {
         throw new ServletException(String.format("Error creating vaading application using [%s]",
            factory.getClass().getName()));
      }
      setApplicationMetaData(request, application);
      HttpSession httpSession = request.getSession();
      final ApplicationSession session = new ApplicationSession(application, httpSession);
      sessions.add(session);
      httpSession.setAttribute(ApplicationSession.class.getName(), new HttpSessionListener() {

         @Override
         public void sessionDestroyed(HttpSessionEvent arg0) {
            session.dispose();
            sessions.remove(session);
         }

         @Override
         public void sessionCreated(HttpSessionEvent arg0) {
            // Do Nothing
         }
      });
      return application;
   }

   @SuppressWarnings("unused")
   private void setApplicationMetaData(HttpServletRequest request, Application application) throws ServletException {

      // TODO: Hook into user admin - or have this done through a filter
      //      application.setUser(user);
      //      application.setLogoutURL(logoutURL);
      //      application.setMainWindow(mainWindow);

      Principal principal = request.getUserPrincipal();
      if (principal == null) {
         principal = new Principal() {

            @Override
            public String getName() {
               return "Guest";
            }

            @Override
            public String toString() {
               return getName();
            }
         };
      }
      //      if (request.isUserInRole("Some Role")) {
      //         application.setUserRole("myRole");
      //      } else {
      //         throw new ServletException("Access Denied");
      //      }
      application.setUser(principal);
      //      application.setLogoutURL(request.getContextPath() + "logout.jsp");
   }

   @Override
   protected URL getApplicationUrl(HttpServletRequest request) throws MalformedURLException {
      String serverName = request.getServerName();
      int serverPort = request.getServerPort();
      String requestUri = request.getRequestURI();

      StringBuilder urlBuilder = new StringBuilder();
      urlBuilder.append(request.getScheme()).append("://");
      urlBuilder.append(request.getServerName());
      if (!(request.getServerPort() == 443 && "https".equals(request.getScheme())) || !(request.getServerPort() == 80 && "http".equals(request.getScheme()))) {
         urlBuilder.append(":").append(request.getServerPort());
      }
      urlBuilder.append(request.getRequestURI());

      final URL reqURL = new URL(urlBuilder.toString());
      String servletPath = getServletPath(request);

      URL url = new URL(reqURL, servletPath);

      if (getLogger().isTraceEnabled()) {
         getLogger().trace("application url ------------------------------------------------------");
         getLogger().trace("application url - serverName[%s] port[%s]", serverName, serverPort);
         getLogger().trace("application url - requestUri[%s]", requestUri);
         getLogger().trace("application url - servletPath[%s]", servletPath);
         getLogger().trace("application url - URL - [%s]", url.toString());
         getLogger().trace("application url ------------------------------------------------------");
      }
      return url;
   }

   private String getServletPath(HttpServletRequest request) {
      String servletPath = "";
      if (request.getAttribute("javax.servlet.include.servlet_path") != null) {
         // this is an include request
         servletPath =
            request.getAttribute("javax.servlet.include.context_path").toString() + request.getAttribute("javax.servlet.include.servlet_path");

      } else {
         servletPath = request.getContextPath() + request.getServletPath();
      }

      if (servletPath.length() == 0 || servletPath.charAt(servletPath.length() - 1) != '/') {
         servletPath = servletPath + "/";
      }
      return servletPath;
   }

   @Override
   protected Class<? extends Application> getApplicationClass() {
      return factory.getApplicationClass();
   }

   @Override
   public void destroy() {
      super.destroy();
      for (ApplicationSession info : sessions) {
         info.dispose();
      }
      sessions.clear();
   }

   private String getApplicationOrSystemProperty(String parameterName, String defaultValue) {
      String val = null;

      // Try application properties
      val = getApplicationProperty(parameterName);
      if (val != null) {
         return val;
      }

      // Try system properties
      val = getSystemProperty(parameterName);
      if (val != null) {
         return val;
      }

      return defaultValue;
   }

   @Override
   protected void writeAjaxPageHtmlVaadinScripts(Window window, String themeName, Application application, final BufferedWriter page, String appUrl, String themeUri, String appId, HttpServletRequest request) throws ServletException, IOException {

      // request widgetset takes precedence (e.g portlet include)
      String requestWidgetset = (String) request.getAttribute(REQUEST_WIDGETSET);
      String sharedWidgetset = (String) request.getAttribute(REQUEST_SHARED_WIDGETSET);
      if (requestWidgetset == null && sharedWidgetset == null) {
         // Use the value from configuration or DEFAULT_WIDGETSET.
         // If no shared widgetset is specified, the default widgetset is
         // assumed to be in the servlet/portlet itself.
         requestWidgetset = getApplicationOrSystemProperty(PARAMETER_WIDGETSET, DEFAULT_WIDGETSET);
      }

      String widgetset;
      String widgetsetBasePath;
      if (requestWidgetset != null) {
         widgetset = requestWidgetset;
         widgetsetBasePath = getWebApplicationsStaticFileLocation(request);
      } else {
         widgetset = sharedWidgetset;
         widgetsetBasePath = getStaticFilesLocation(request);
      }

      widgetset = stripSpecialChars(widgetset);

      String widgetsetFilePath =
         widgetsetBasePath + "/" + WIDGETSET_DIRECTORY_PATH + widgetset + "/" + widgetset + ".nocache.js?" + new Date().getTime();

      if (getLogger().isTraceEnabled()) {
         getLogger().trace("writeAjaxPageHtmlVaadinScripts ------------------------------------------------------");
         getLogger().trace("writeAjaxPageHtmlVaadinScripts - widgetset[%s]", widgetset);
         getLogger().trace("writeAjaxPageHtmlVaadinScripts - widgetsetBasePath[%s]", widgetsetBasePath);
         getLogger().trace("writeAjaxPageHtmlVaadinScripts - widgetsetFilePath[%s]", widgetsetFilePath);
         getLogger().trace("writeAjaxPageHtmlVaadinScripts ------------------------------------------------------");
      }

      // Get system messages
      Application.SystemMessages systemMessages = null;
      try {
         systemMessages = getSystemMessages();
      } catch (SystemMessageException e) {
         // failing to get the system messages is always a problem
         throw new ServletException("CommunicationError!", e);
      }

      // Start - Create hidden image tags so reverse proxy can update paths
      page.write("<img id='applicationElementURI' style=\"position:absolute;width:0;height:0;border:0;overflow:hidden;\" src='" + appUrl + "'/>\n");
      page.write("<img id='themeElementURI' style=\"position:absolute;width:0;height:0;border:0;overflow:hidden;\" ");
      page.write(themeUri != null ? "src='" + themeUri + "' " : "src='null' ");
      page.write("/>\n");

      page.write("<script type=\"text/javascript\">\n");
      page.write("//<![CDATA[\n");
      page.write("var applicationElementURI = document.getElementById('applicationElementURI');\n");
      page.write("var themeElementURI = document.getElementById('themeElementURI');\n");
      page.write("//]]>\n</script>\n");
      // End -

      page.write("<script type=\"text/javascript\">\n");
      page.write("//<![CDATA[\n");
      page.write("if(!vaadin || !vaadin.vaadinConfigurations) {\n " + //
      "if(!vaadin) { var vaadin = {}} \n" + //
      "vaadin.vaadinConfigurations = {};\n" + //
      "if (!vaadin.themesLoaded) { vaadin.themesLoaded = {}; }\n");

      if (!isProductionMode()) {
         page.write("vaadin.debug = true;\n");
      }
      page.write("document.write('<iframe tabIndex=\"-1\" id=\"__gwt_historyFrame\" " + //
      "style=\"position:absolute;width:0;height:0;border:0;overflow:hidden;\" src=\"javascript:false\"></iframe>');\n");

      page.write("document.write(\"<script language='javascript' src='" + widgetsetFilePath + "'><\\/script>\");\n}\n");

      page.write("vaadin.vaadinConfigurations[\"" + appId + "\"] = {");

      // Get Application URL from application element source attribute
      page.write("appUri: applicationElementURI.src, ");

      if (window != application.getMainWindow()) {
         page.write("windowName: \"" + JsonPaintTarget.escapeJSON(window.getName()) + "\", ");
      }
      if (isStandalone()) {
         page.write("standalone: true, ");
      }

      // Get theme URI from theme element source attribute
      page.write("themeUri: themeElementURI.src");

      page.write(", versionInfo : {vaadinVersion:\"");
      page.write(VERSION);
      page.write("\",applicationVersion:\"");
      page.write(JsonPaintTarget.escapeJSON(application.getVersion()));
      page.write("\"}");
      if (systemMessages != null) {
         // Write the CommunicationError -message to client
         String caption = systemMessages.getCommunicationErrorCaption();
         if (caption != null) {
            caption = "\"" + JsonPaintTarget.escapeJSON(caption) + "\"";
         }
         String message = systemMessages.getCommunicationErrorMessage();
         if (message != null) {
            message = "\"" + JsonPaintTarget.escapeJSON(message) + "\"";
         }
         String url = systemMessages.getCommunicationErrorURL();
         if (url != null) {
            url = "\"" + JsonPaintTarget.escapeJSON(url) + "\"";
         }

         page.write(",\"comErrMsg\": {" + "\"caption\":" + caption + "," + "\"message\" : " + message + "," + "\"url\" : " + url + "}");

         // Write the AuthenticationError -message to client
         caption = systemMessages.getAuthenticationErrorCaption();
         if (caption != null) {
            caption = "\"" + JsonPaintTarget.escapeJSON(caption) + "\"";
         }
         message = systemMessages.getAuthenticationErrorMessage();
         if (message != null) {
            message = "\"" + JsonPaintTarget.escapeJSON(message) + "\"";
         }
         url = systemMessages.getAuthenticationErrorURL();
         if (url != null) {
            url = "\"" + JsonPaintTarget.escapeJSON(url) + "\"";
         }

         page.write(",\"authErrMsg\": {" + "\"caption\":" + caption + "," + "\"message\" : " + message + "," + "\"url\" : " + url + "}");
      }
      page.write("};\n//]]>\n</script>\n");

      if (themeName != null) {
         // Custom theme's stylesheet, load only once, in different
         // script
         // tag to be dominate styles injected by widget
         // set
         page.write("<script type=\"text/javascript\">\n");
         page.write("//<![CDATA[\n");
         page.write("if(!vaadin.themesLoaded['" + themeName + "']) {\n");
         page.write("var stylesheet = document.createElement('link');\n");
         page.write("stylesheet.setAttribute('rel', 'stylesheet');\n");
         page.write("stylesheet.setAttribute('type', 'text/css');\n");

         // Get theme uri from theme element source
         page.write("var myHref = themeElementURI.src + \"/styles.css\";\n");
         page.write("stylesheet.setAttribute('href', myHref);\n");
         page.write("document.getElementsByTagName('head')[0].appendChild(stylesheet);\n");
         page.write("vaadin.themesLoaded['" + themeName + "'] = true;\n}\n");
         page.write("//]]>\n</script>\n");
      }

      // Warn if the widgetset has not been loaded after 15 seconds on
      // inactivity
      page.write("<script type=\"text/javascript\">\n");
      page.write("//<![CDATA[\n");
      page.write("setTimeout('if (typeof " + widgetset.replace('.', '_') + " == \"undefined\") {alert(\"Failed to load the widgetset: " + widgetsetFilePath + "\")};',15000);\n" + "//]]>\n</script>\n");
   }

   private String getWebApplicationsStaticFileLocation(HttpServletRequest request) {
      String staticFileLocation;
      // if property is defined in configurations, use that
      staticFileLocation = getApplicationOrSystemProperty(PARAMETER_VAADIN_RESOURCES, null);
      if (staticFileLocation != null) {
         return staticFileLocation;
      }

      // the last (but most common) option is to generate default location
      // from request

      // if context is specified add it to widgetsetUrl
      String ctxPath = request.getContextPath();

      // FIXME: ctxPath.length() == 0 condition is probably unnecessary and
      // might even be wrong.

      if (ctxPath.length() == 0 && request.getAttribute("javax.servlet.include.context_path") != null) {
         // include request (e.g portlet), get context path from
         // attribute
         ctxPath = (String) request.getAttribute("javax.servlet.include.context_path");
      }

      // Remove heading and trailing slashes from the context path
      ctxPath = removeHeadingOrTrailing(ctxPath, "/");

      if (ctxPath.equals("")) {
         return "";
      } else {
         return "/" + ctxPath;
      }
   }

   private static String removeHeadingOrTrailing(String string, String what) {
      while (string.startsWith(what)) {
         string = string.substring(1);
      }

      while (string.endsWith(what)) {
         string = string.substring(0, string.length() - 1);
      }

      return string;
   }
}
