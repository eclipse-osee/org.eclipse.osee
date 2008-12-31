/*
 * Created on Dec 26, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workflow.editor.parts;

import java.beans.PropertyChangeEvent;
import java.util.logging.Level;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.Request;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.workflow.editor.model.ReturnConnection;
import org.eclipse.osee.ats.workflow.editor.model.WorkPageShape;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class WorkPageEditPart extends ShapeEditPart {

   private final WorkPageShape workPageShape;
   private RightAnchor returnAnchor;

   public WorkPageEditPart(WorkPageShape workPageShape) {
      this.workPageShape = workPageShape;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.config.editor.parts.ShapeEditPart#createFigure()
    */
   @Override
   protected IFigure createFigure() {
      IFigure f = super.createFigure();
      f.setLayoutManager(new GridLayout());
      try {
         if (workPageShape.isCompletedState()) {
            f.setBackgroundColor(ColorConstants.darkGreen);
         } else if (workPageShape.isCancelledState()) {
            f.setBackgroundColor(ColorConstants.lightGray);
         } else if (workPageShape.isStartPage()) {
            f.setBackgroundColor(ColorConstants.yellow);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      f.add(new Label(workPageShape.getName()));
      f.setToolTip(new Label(workPageShape.getToolTip()));
      return f;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.workflow.editor.parts.ShapeEditPart#propertyChange(java.beans.PropertyChangeEvent)
    */
   @Override
   public void propertyChange(PropertyChangeEvent evt) {
      super.propertyChange(evt);
      String prop = evt.getPropertyName();
      if (WorkPageShape.START_PAGE.equals(prop)) {
         if (workPageShape.isStartPage()) {
            getFigure().setBackgroundColor(ColorConstants.yellow);
         } else {
            getFigure().setBackgroundColor(ColorConstants.green);
         }
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.config.editor.parts.ShapeEditPart#getTargetConnectionAnchor(org.eclipse.gef.Request)
    */
   @Override
   public ConnectionAnchor getTargetConnectionAnchor(Request request) {
      // TODO implement sending back returnAnchor if appropriate
      return super.getTargetConnectionAnchor(request);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.config.editor.parts.ShapeEditPart#getTargetConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
    */
   @Override
   public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
      if (connection.getModel() instanceof ReturnConnection) {
         if (returnAnchor == null) {
            returnAnchor = new RightAnchor(getFigure());
         }
         return returnAnchor;
      }
      return super.getTargetConnectionAnchor(connection);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.config.editor.parts.ShapeEditPart#getSourceConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
    */
   @Override
   public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
      if (connection.getModel() instanceof ReturnConnection) {
         if (returnAnchor == null) {
            returnAnchor = new RightAnchor(getFigure());
         }
         return returnAnchor;
      }
      return super.getTargetConnectionAnchor(connection);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.config.editor.parts.ShapeEditPart#getSourceConnectionAnchor(org.eclipse.gef.Request)
    */
   @Override
   public ConnectionAnchor getSourceConnectionAnchor(Request request) {
      // TODO implement sending back returnAnchor if appropriate
      return super.getSourceConnectionAnchor(request);
   }

}
