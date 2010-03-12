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

import net.jini.core.lookup.ServiceRegistrar;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osee.framework.jdk.core.type.TreeObject;
import org.eclipse.osee.framework.jdk.core.type.TreeParent;
import org.eclipse.osee.framework.ui.service.control.data.IJiniService;
import org.eclipse.osee.framework.ui.service.control.data.ReggieMonitorServiceNode;
import org.eclipse.osee.framework.ui.service.control.renderer.IRenderer;
import org.eclipse.osee.framework.ui.service.control.widgets.IServiceManager;
import org.eclipse.osee.framework.ui.service.control.widgets.LookupViewer;
import org.eclipse.osee.framework.ui.swt.FormattedText;

/**
 * @author Roberto E. Escobar
 */
public class NodeSelected implements ISelectionChangedListener {

   private IServiceManager<TreeParent> mainWindow;
   private FormattedText textArea;
   private LookupViewer lookupViewer;

   public NodeSelected(IServiceManager<TreeParent> mainWindow) {
      super();
      this.mainWindow = mainWindow;
      this.mainWindow.getServicesViewer().getViewer().addSelectionChangedListener(this);
      this.textArea = this.mainWindow.getQuickViewer();
      this.lookupViewer = this.mainWindow.getLookupViewer();
      if (lookupViewer != null) {
         lookupViewer.getViewer().addSelectionChangedListener(this);
      }

   }

   public void selectionChanged(SelectionChangedEvent event) {
      StructuredSelection selection = (StructuredSelection) event.getSelection();
      if (!selection.isEmpty()) {
         Object selectedObject = selection.getFirstElement();
         if (selectedObject instanceof TreeObject) {
            handleTreeSelection((TreeObject) selectedObject);
         } else if (selectedObject instanceof ServiceRegistrar) {
            handleLookupSelection((ServiceRegistrar) selectedObject);
         }
      }
   }

   private void handleTreeSelection(TreeObject treeObject) {
      if (treeObject instanceof IRenderer) {
         ((IRenderer) treeObject).renderInComposite(textArea);
      }

      if (treeObject instanceof IJiniService) {
         if (lookupViewer != null) {

            String serviceId = null;
            if (treeObject instanceof ReggieMonitorServiceNode) {
               serviceId = ((ReggieMonitorServiceNode) treeObject).getSpawnedReggieServiceId();
            } else {
               serviceId = ((IJiniService) treeObject).getServiceID().toString();
            }

            lookupViewer.setSelection(serviceId);
         }
      }
   }

   private void handleLookupSelection(ServiceRegistrar serviceRegistrar) {
      if (lookupViewer != null) {
         IRenderer renderer = this.mainWindow.getLookupViewer().getRenderer(serviceRegistrar.getServiceID());
         if (renderer != null) {
            renderer.renderInComposite(textArea);
         }
      }
   }
}
