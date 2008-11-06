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
package org.eclipse.osee.framework.ui.service.control.renderer;

import java.rmi.RemoteException;
import java.util.Set;
import java.util.TreeSet;
import net.jini.core.lookup.ServiceID;
import net.jini.core.lookup.ServiceRegistrar;
import org.eclipse.osee.framework.plugin.core.config.JiniLookupGroupConfig;
import org.eclipse.osee.framework.ui.swt.FormattedText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Roberto E. Escobar
 */
public class ReggieItemHandler implements IRenderer {

   private static final Set<String> allowedGroups = new TreeSet<String>();;

   static {
      populteAllowedGroups();
   }

   private int port;
   private String host;
   private ServiceID id;
   private String className;
   private String[] groups;

   public ReggieItemHandler(ServiceRegistrar reggie) {
      super();
      parseReggie(reggie);
   }

   public String getClassName() {
      return className;
   }

   public String[] getGroups() {
      return groups;
   }

   public String getHost() {
      return host;
   }

   public ServiceID getId() {
      return id;
   }

   public int getPort() {
      return port;
   }

   public Control renderInComposite(Composite parent) {
      if (parent instanceof FormattedText) {
         FormattedText textArea = (FormattedText) parent;
         textArea.clearTextArea();
         textArea.addText("\t" + "Service" + ": ", SWT.BOLD, SWT.COLOR_DARK_BLUE);
         textArea.addText(className + "\n", SWT.NORMAL, SWT.COLOR_BLACK);

         textArea.addText("\t" + "Host" + ": ", SWT.BOLD, SWT.COLOR_DARK_BLUE);
         textArea.addText(host + "\n", SWT.NORMAL, SWT.COLOR_BLACK);

         textArea.addText("\t" + "Port" + ": ", SWT.BOLD, SWT.COLOR_DARK_BLUE);
         textArea.addText(port + "\n", SWT.NORMAL, SWT.COLOR_BLACK);

         textArea.addText("\t" + "Groups" + ": ", SWT.BOLD, SWT.COLOR_DARK_BLUE);

         String groupsToDisplay = "";
         if (groups != null) {
            for (int index = 0; index < groups.length; index++) {
               String group = groups[index];
               groupsToDisplay += (group != null && group.length() > 0 ? group : "Public");
               if (index + 1 < groups.length) {
                  groupsToDisplay += ", ";
               }
            }
         }

         textArea.addText("{" + groupsToDisplay + "}\n", SWT.BOLD, SWT.COLOR_DARK_GREEN);
         textArea.addText("\t" + "ID" + ": ", SWT.BOLD, SWT.COLOR_DARK_BLUE);
         textArea.addText(id + "\n", SWT.NORMAL, SWT.COLOR_BLACK);
      }
      return parent;
   }

   private void parseReggie(ServiceRegistrar reggie) {
      try {
         className = reggie.getLocator().getClass().getName();
         port = reggie.getLocator().getPort();
         host = reggie.getLocator().getHost();
         id = reggie.getServiceID();
         groups = reggie.getGroups();
      } catch (RemoteException ex) {
         ex.printStackTrace();
      }
   }

   private static void populteAllowedGroups() {
      allowedGroups.clear();
      String[] tempGroups = JiniLookupGroupConfig.getOseeJiniServiceGroups();
      for (String toStore : tempGroups) {
         if (!allowedGroups.contains(toStore)) {
            allowedGroups.add(toStore);
         }
      }
   }

   public static boolean isAllowed(ServiceRegistrar reggie) {
      try {
         return isAllowed(reggie.getGroups());
      } catch (RemoteException ex) {
         ex.printStackTrace();
      }
      return false;
   }

   private static boolean isAllowed(String[] groups) {
      if (groups != null) {
         for (String toCheck : groups) {
            if (allowedGroups.contains(toCheck)) {
               return true;
            }
         }
      }
      return false;
   }

   public static Set<String> getAllowedGroups() {
      return allowedGroups;
   }
}
