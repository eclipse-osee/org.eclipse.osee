/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.doors.connector.ui.handler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.logging.Level;
import org.apache.commons.httpclient.Cookie;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.doors.connector.core.DoorsArtifact;
import org.eclipse.osee.doors.connector.core.DoorsModel;
import org.eclipse.osee.doors.connector.core.DoorsOSLCConnector;
import org.eclipse.osee.doors.connector.core.LoginDialog;
import org.eclipse.osee.doors.connector.core.ServiceProvider;
import org.eclipse.osee.doors.connector.core.oauth.DWAOAuthService;
import org.eclipse.osee.doors.connector.ui.oauth.extension.DoorsOSLCDWAProviderInfoExtn;
import org.eclipse.osee.doors.connector.ui.perspectives.Doors;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

/**
 * Handler class to get the Doors Artifact from DWA responses
 *
 * @author Chandan Bandemutt
 */
public class DoorsOSLCUIHandler extends AbstractHandler {

   /**
    *
    */
   private static final String JAVA_SCRIPT_HTML = "JavaScript.html";
   private String selectionDialogUrl;
   private final String fileName =
      ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString() + File.separator + JAVA_SCRIPT_HTML;

   /**
    * {@inheritDoc}
    */
   @Override
   public Object execute(final ExecutionEvent event) {

      DoorsArtifact doorsArtifact1 = DoorsModel.getDoorsArtifact();
      if (doorsArtifact1 == null) {
         LoginDialog dialog = new LoginDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
         if (dialog.open() == Window.OK) {
            DoorsOSLCDWAProviderInfoExtn config = new DoorsOSLCDWAProviderInfoExtn();
            DoorsOSLCConnector connector = new DoorsOSLCConnector();
            DWAOAuthService service1 = new DWAOAuthService(config, "Open System Engineering Environment", "BoeingOSEE");
            DoorsArtifact doorsArtifact = connector.getAuthentication(service1, dialog.getName(), dialog.getPassword());
            DoorsModel.setDoorsArtifact(doorsArtifact);

            Cookie[] cookies = service1.getHttpClient().getState().getCookies();
            for (Cookie cookie : cookies) {
               DoorsModel.setJSessionID(cookie.getValue());
            }
         }

         DoorsArtifact doorsArtifact = DoorsModel.getDoorsArtifact();

         if (doorsArtifact != null) {
            ServiceProvider serviceProvider = getServiceProvider(doorsArtifact);

            this.selectionDialogUrl = serviceProvider.getSelectionDialogUrl();

            DoorsOSLCDWAProviderInfoExtn config = new DoorsOSLCDWAProviderInfoExtn();

            DoorsModel.setDialogUrl(this.selectionDialogUrl);

            String html =
               "<!DOCTYPE html><html><head> <meta charset=\"utf-8\"/> <title>HTML5 Cross Document Demo</title> " + "<script>	var b;window.addEventListener(\"message\",handleMessage,true);function handleMessage(e) " + "{if (e.origin !== \"" + config.DWAHostName() + "\")" + "{ return;} " + " b = e.data; " + "theJavaFunction(b);}" + "</script></head><body>" + "<div id=\"test\"></div>" + "<iframe src= \"" + this.selectionDialogUrl + "#oslc-core-postMessage-1.0" + "\"" + "width=\"900\" height=\"600\" id=\"iframe\"></iframe>" + "</body></html>";

            try {
               FileWriter fstream = new FileWriter(this.fileName);
               BufferedWriter out = new BufferedWriter(fstream);
               out.write(html);
               out.close();
            } catch (Exception e) {
               OseeLog.log(DWAOAuthService.class, Level.SEVERE, "Error: " + e.getMessage());
            }

            try {
               PlatformUI.getWorkbench().showPerspective("org.eclipse.osee.doors.connector.ui.perspective",
                  PlatformUI.getWorkbench().getActiveWorkbenchWindow());

               IViewPart findView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(
                  "org.eclipse.osee.doors.connector.ui.Doors");
               if (findView instanceof Doors) {
                  ((Doors) findView).refresh();
               }

            } catch (WorkbenchException e) {
               OseeLog.log(getClass(), Level.SEVERE, e);
            }
         }
      } else {
         try {
            PlatformUI.getWorkbench().showPerspective("org.eclipse.osee.doors.connector.ui.perspective",
               PlatformUI.getWorkbench().getActiveWorkbenchWindow());
         } catch (WorkbenchException e) {
            OseeLog.log(getClass(), Level.SEVERE, e);
         }
      }
      return null;
   }

   private ServiceProvider getServiceProvider(final DoorsArtifact doorsArtifact) {
      if (doorsArtifact != null) {
         List<DoorsArtifact> children = doorsArtifact.getChildren();
         ServiceProvider provider = null;
         for (DoorsArtifact doorsArtifact2 : children) {
            if (doorsArtifact2 instanceof ServiceProvider) {
               provider = (ServiceProvider) doorsArtifact2;
               return provider;
            }
            provider = getServiceProvider(doorsArtifact2);
         }
         return provider;
      }
      return null;
   }

}
