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
package org.eclipse.osee.framework.ui.data.model.editor;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.tools.SelectionTool;
import org.eclipse.osee.framework.ui.data.model.editor.part.DataTypeEditPart;
import org.eclipse.swt.events.MouseEvent;

/**
 * @author Roberto E. Escobar
 */
public class ODMEditorSelectionTool extends SelectionTool {

   public ODMEditorSelectionTool() {
      super();
   }

   public void mouseUp(MouseEvent e, EditPartViewer viewer) {
      if (getTargetEditPart() instanceof DataTypeEditPart) {
         super.mouseUp(e, viewer);
      }
   }

   public void mouseDown(MouseEvent e, EditPartViewer viewer) {
      if (getTargetEditPart() instanceof DataTypeEditPart) {
         super.mouseDown(e, viewer);
      }
   }

}
