/*
 * Created on Feb 5, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.data.model.editor.policy;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.osee.framework.ui.data.model.editor.command.DataTypeCreateCommand;
import org.eclipse.osee.framework.ui.data.model.editor.command.DataTypeMoveResizeConstraintCommand;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.ODMGraph;
import org.eclipse.osee.framework.ui.data.model.editor.part.DataTypeEditPart;

/**
 * @author Roberto E. Escobar
 */
public class ODMLayoutEditPolicy extends XYLayoutEditPolicy {

   /* (non-Javadoc)
    * @see ConstrainedLayoutEditPolicy#createChangeConstraintCommand(ChangeBoundsRequest, EditPart, Object)
    */
   protected Command createChangeConstraintCommand(ChangeBoundsRequest request, EditPart child, Object constraint) {
      if (child instanceof DataTypeEditPart && constraint instanceof Rectangle) {
         //          return a command that can move and/or resize a Shape
         return new DataTypeMoveResizeConstraintCommand((DataType) child.getModel(), request, (Rectangle) constraint);
      }
      return super.createChangeConstraintCommand(request, child, constraint);
   }

   /* (non-Javadoc)
    * @see ConstrainedLayoutEditPolicy#createChangeConstraintCommand(EditPart, Object)
    */
   protected Command createChangeConstraintCommand(EditPart child, Object constraint) {
      // not used in this example
      return null;
   }

   /* (non-Javadoc)
    * @see LayoutEditPolicy#getCreateCommand(CreateRequest)
    */
   protected Command getCreateCommand(CreateRequest request) {
      Object childClass = request.getNewObjectType();
      if (childClass == DataType.class) {
         return new DataTypeCreateCommand((DataType) request.getNewObject(), (ODMGraph) getHost().getModel(),
               (Rectangle) getConstraintFor(request));
      }
      return null;
   }

}
