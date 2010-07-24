/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.ui.message.tree;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.ote.ui.message.messageXViewer.MessageXViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

public class MessageWatchLabelProvider extends XViewerLabelProvider {

   public MessageWatchLabelProvider(MessageXViewer viewer) {
      super(viewer);
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn col, int columnIndex) {
      assert element instanceof AbstractTreeNode;
      return ((AbstractTreeNode) element).getImage(col);
   }

   @Override
   public void addListener(ILabelProviderListener listener) {
   }

   @Override
   public void dispose() {

   }

   @Override
   public boolean isLabelProperty(Object element, String property) {
      return false;
   }

   @Override
   public void removeListener(ILabelProviderListener listener) {
   }

   @Override
   public String getColumnText(Object element, XViewerColumn col, int columnIndex) throws Exception {
      return ((AbstractTreeNode) element).getLabel(col);
   }

   @Override
   public Color getForeground(Object element, XViewerColumn col, int columnIndex) {
      AbstractTreeNode node = (AbstractTreeNode) element;
      return node.isEnabled() ? null : Displays.getSystemColor(SWT.COLOR_RED);
   }
}
