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
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.osee.framework.jdk.core.type.TreeParent;
import org.eclipse.osee.framework.ui.service.control.widgets.IServiceManager;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.TableItem;

/**
 * @author Roberto E. Escobar
 */
public class UpdateLookupViewerToolTip implements MouseTrackListener {

   private IServiceManager<TreeParent> mainWindow;
   private StructuredViewer structuredViewer;

   public UpdateLookupViewerToolTip(IServiceManager<TreeParent> mainWindow) {
      super();
      this.mainWindow = mainWindow;
      this.mainWindow.getLookupViewer().getViewer().getControl().addMouseTrackListener(this);
      structuredViewer = this.mainWindow.getLookupViewer().getViewer();
   }

   public void mouseEnter(MouseEvent e) {
      structuredViewer.getControl().setToolTipText("Displays JINI Lookup Servers");
   }

   public void mouseExit(MouseEvent e) {
      structuredViewer.getControl().setToolTipText("Displays JINI Lookup Servers");
   }

   public void mouseHover(MouseEvent e) {
      String toSet = "";
      Point point = new Point(e.x, e.y);
      TableItem tableItem = ((TableViewer) structuredViewer).getTable().getItem(point);
      if (tableItem != null) {
         Object elementData = tableItem.getData();
         if (elementData instanceof ServiceRegistrar) {
            toSet = "Double-Click to explorer services that are registered ONLY on this lookup server.";
            structuredViewer.getControl().setToolTipText(toSet);
         }
      }
   }
}
