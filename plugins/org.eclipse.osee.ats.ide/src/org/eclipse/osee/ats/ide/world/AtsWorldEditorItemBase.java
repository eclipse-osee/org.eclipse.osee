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
package org.eclipse.osee.ats.ide.world;

import java.util.Collections;
import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.ide.workflow.task.TaskXViewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public abstract class AtsWorldEditorItemBase implements IAtsWorldEditorItem {

   @Override
   public Image getColumnImage(Object element, XViewerColumn col, int columnIndex) {
      return null;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn col, int columnIndex) {
      return null;
   }

   @Override
   public Color getForeground(Object element, XViewerColumn col, int columnIndex) {
      return null;
   }

   @Override
   public List<XViewerColumn> getXViewerColumns() {
      return Collections.emptyList();
   }

   @Override
   public boolean isXColumnProvider(XViewerColumn col) {
      return false;
   }

   @Override
   public List<? extends Action> getWorldEditorMenuActions(IWorldEditorProvider worldEditorProvider, WorldEditor worldEditor) {
      return Collections.emptyList();
   }

   @Override
   public void updateTaskEditMenuActions(TaskXViewer taskXViewer) {
      // do nothing
   }

}
