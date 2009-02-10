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
package org.eclipse.osee.ats.workflow.editor.actions;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.SharedCursors;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.tools.SelectionTool;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.workflow.editor.model.ModelElement;
import org.eclipse.osee.ats.workflow.editor.parts.DiagramEditPart;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.swt.events.MouseEvent;

/**
 * @author Donald G. Dunne
 */
public class ValidateDiagramTool extends SelectionTool {

   public ValidateDiagramTool() {
      setDefaultCursor(SharedCursors.ARROW);
   }

   /* (non-Javadoc)
    * @see org.eclipse.gef.tools.SelectionTool#mouseDown(org.eclipse.swt.events.MouseEvent, org.eclipse.gef.EditPartViewer)
    */
   @Override
   public void mouseDown(MouseEvent e, EditPartViewer viewer) {
      super.mouseDown(e, viewer);
      try {
         EditPart editPart = getTargetEditPart();
         Object model = editPart.getModel();
         if (editPart instanceof ScalableFreeformRootEditPart) {
            EditPart ePart = ((ScalableFreeformRootEditPart) editPart).getContents();
            if (ePart instanceof DiagramEditPart) {
               model = ((DiagramEditPart) ePart).getModel();
            }
         }
         if (model instanceof ModelElement) {
            Result result = ((ModelElement) model).validForSave();
            if (result.isFalse()) {
               AWorkbench.popup("Validate Error", model + "\n\n" + "Error: " + result.getText());
            } else {
               AWorkbench.popup("Validate Success", model + "\n\n" + "Valid");
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}
