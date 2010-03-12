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

import java.rmi.RemoteException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osee.framework.jdk.core.type.TreeObject;
import org.eclipse.osee.framework.jdk.core.type.TreeParent;
import org.eclipse.osee.framework.jini.service.interfaces.IService;
import org.eclipse.osee.framework.ui.service.control.data.ServiceNode;
import org.eclipse.osee.framework.ui.service.control.widgets.IServiceManager;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * @author Roberto E. Escobar
 */
public class KillServiceAction extends Action implements ISelectionChangedListener {

   private IServiceManager<TreeParent> mainWindow;

   public KillServiceAction(IServiceManager<TreeParent> mainWindow) {
      super();
      this.mainWindow = mainWindow;
      mainWindow.getServicesViewer().getViewer().addSelectionChangedListener(this);

      setText("Kill Service");
      setToolTipText("Shutdown the selected service.");
      setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
            ISharedImages.IMG_OBJS_ERROR_TSK));
   }

   public void run() {
      ISelection sel = mainWindow.getServicesViewer().getViewer().getSelection();
      if (!sel.isEmpty()) {
         TreeObject treeObject = (TreeObject) ((StructuredSelection) sel).getFirstElement();
         if (treeObject instanceof ServiceNode) {
            ServiceNode serviceNode = ((ServiceNode) treeObject);
            Object service = serviceNode.getServiceItem().service;
            if (service instanceof IService) {
               try {
                  ((IService) service).kill();
               } catch (RemoteException ex) {
                  MessageDialog.openError(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        "Kill Service Error",
                        "Unable to kill [" + serviceNode.getName() + "] service.\n" + "Service may no longer be available.");
               }
            }
         }
      }
   }

   public void selectionChanged(SelectionChangedEvent event) {
      StructuredSelection selection = (StructuredSelection) event.getSelection();
      if (!selection.isEmpty()) {
         TreeObject selectedObject = (TreeObject) selection.getFirstElement();
         if (selectedObject instanceof ServiceNode) {
            ServiceNode serviceNode = ((ServiceNode) selectedObject);
            Object service = serviceNode.getServiceItem().service;
            if (service instanceof IService) {
               this.setEnabled(true);
            } else {
               this.setEnabled(false);
            }
         } else {
            this.setEnabled(false);
         }
      }
   }
}
