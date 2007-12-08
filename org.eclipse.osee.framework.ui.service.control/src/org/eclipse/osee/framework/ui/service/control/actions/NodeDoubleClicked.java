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
package org.eclipse.osee.framework.ui.service.control.actions;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osee.framework.jdk.core.type.TreeObject;
import org.eclipse.osee.framework.ui.service.control.data.ServiceNode;
import org.eclipse.osee.framework.ui.service.control.managers.ConnectionManager;
import org.eclipse.osee.framework.ui.service.control.managers.ServiceConnectionException;
import org.eclipse.osee.framework.ui.service.control.widgets.ManagerMain;
import org.eclipse.ui.PlatformUI;

/**
 * @author Roberto E. Escobar
 */
public class NodeDoubleClicked implements IDoubleClickListener {

   private ManagerMain mainWindow;
   private ConnectionManager connectionManager;

   public NodeDoubleClicked(ManagerMain mainWindow) {
      super();
      this.mainWindow = mainWindow;
      this.connectionManager = mainWindow.getConnectionManager();
      this.mainWindow.getServicesViewer().getViewer().addDoubleClickListener(this);
   }

   public void doubleClick(DoubleClickEvent event) {
      ISelection sel = event.getSelection();
      if (!sel.isEmpty()) {
         TreeObject treeObject = (TreeObject) ((StructuredSelection) sel).getFirstElement();
         if (treeObject instanceof ServiceNode) {
            ServiceNode serviceNode = (ServiceNode) treeObject;
            if (connectionManager.isAllowedConnectionType(serviceNode.getServiceItem())) {
               try {
                  connectionManager.attemptConnection(serviceNode);
               } catch (ServiceConnectionException ex) {
                  MessageDialog.openError(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        "Unable to Connect",
                        "Unable to connect to [" + serviceNode.getName() + "] service.\n" + "Service may no longer be available.");
               }
            } else {
               MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                     "Unsupported Connection", "Connection is not allowed for [" + serviceNode.getName() + "] Service.");
            }
         }
      }
   }
}
