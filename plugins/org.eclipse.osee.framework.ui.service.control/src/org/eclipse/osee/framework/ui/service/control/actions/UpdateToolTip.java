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

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.jdk.core.type.TreeParent;
import org.eclipse.osee.framework.ui.service.control.data.CategoryParent;
import org.eclipse.osee.framework.ui.service.control.data.ReggieMonitorServiceNode;
import org.eclipse.osee.framework.ui.service.control.data.ServiceNode;
import org.eclipse.osee.framework.ui.service.control.managers.ConnectionManager;
import org.eclipse.osee.framework.ui.service.control.widgets.IServiceManager;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Roberto E. Escobar
 */
public class UpdateToolTip implements MouseTrackListener {

   private IServiceManager<TreeParent> mainWindow;
   private TreeViewer treeViewer;

   public UpdateToolTip(IServiceManager<TreeParent> mainWindow) {
      super();
      this.mainWindow = mainWindow;
      this.mainWindow.getServicesViewer().getViewer().getControl().addMouseTrackListener(this);
      treeViewer = (TreeViewer) this.mainWindow.getServicesViewer().getViewer();
   }

   private String handleCategorySelection(CategoryParent category) {
      int numberOfItems = category.getChildren().length;
      String name = category.getName();
      return name + ": " + numberOfItems + (numberOfItems == 1 ? " service" : " services");
   }

   public void mouseEnter(MouseEvent e) {
      mainWindow.getServicesViewer().getViewer().getControl().setToolTipText("Displays JINI services");
   }

   public void mouseExit(MouseEvent e) {
      mainWindow.getServicesViewer().getViewer().getControl().setToolTipText("Displays JINI services");
   }

   public void mouseHover(MouseEvent e) {
      String toSet = "";
      Point point = new Point(e.x, e.y);
      TreeItem treeItem = treeViewer.getTree().getItem(point);
      if (treeItem != null) {
         Object elementData = treeItem.getData();
         if (elementData instanceof CategoryParent) {
            toSet = handleCategorySelection((CategoryParent) elementData);
            mainWindow.getServicesViewer().getViewer().getControl().setToolTipText(toSet);
         } else if (elementData instanceof ReggieMonitorServiceNode) {
            ReggieMonitorServiceNode monitorNode = (ReggieMonitorServiceNode) elementData;

            toSet =
                  "Right-Click and select Kill from pop-up menu \n" + "to terminate Lookup Server.\n" + "On: " + monitorNode.getSpawnedReggieOnHost() + "\n" + "Service Id: " + monitorNode.getSpawnedReggieServiceId();
            mainWindow.getServicesViewer().getViewer().getControl().setToolTipText(toSet);
         } else if (elementData instanceof ServiceNode) {
            ServiceNode serviceNode = (ServiceNode) elementData;
            if (ConnectionManager.getInstance().isAllowedConnectionType(serviceNode.getServiceItem())) {
               toSet = "Double-Click to interact with " + serviceNode.getName();
               mainWindow.getServicesViewer().getViewer().getControl().setToolTipText(toSet);
            }
         }
      }
   }
}
