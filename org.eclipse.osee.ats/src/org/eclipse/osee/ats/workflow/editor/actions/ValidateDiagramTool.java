/*
 * Created on Dec 31, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
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
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
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
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
   }

}
