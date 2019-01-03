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
package org.eclipse.osee.ats.ide.workdef.viewer.parts;

import java.beans.PropertyChangeEvent;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.requests.SelectionRequest;
import org.eclipse.osee.ats.ide.workdef.viewer.EditAction;
import org.eclipse.osee.ats.ide.workdef.viewer.model.ReturnTransitionConnection;
import org.eclipse.osee.ats.ide.workdef.viewer.model.StateDefShape;

/**
 * @author Donald G. Dunne
 */
public class StateDefEditPart extends ShapeEditPart {

   private final StateDefShape stateDefShape;
   private RightAnchor returnAnchor;
   private Label label;

   public StateDefEditPart(StateDefShape stateDefShape) {
      this.stateDefShape = stateDefShape;
   }

   @Override
   protected IFigure createFigure() {
      IFigure f = super.createFigure();
      f.setLayoutManager(new GridLayout());
      if (stateDefShape.isCompletedState()) {
         f.setBackgroundColor(ColorConstants.darkGreen);
      } else if (stateDefShape.isCancelledState()) {
         f.setBackgroundColor(ColorConstants.lightGray);
      } else if (stateDefShape.isStartState()) {
         f.setBackgroundColor(ColorConstants.yellow);
      }
      label = new Label(stateDefShape.getName());
      f.add(label);
      f.setToolTip(new Label(stateDefShape.getToolTip()));
      return f;
   }

   @Override
   protected void refreshVisuals() {
      super.refreshVisuals();
      label.setText(stateDefShape.getName());
   }

   @Override
   public void performRequest(Request req) {
      super.performRequest(req);
      System.out.println(req);
      if (req instanceof SelectionRequest) {
         new EditAction().run();
      }
   }

   @Override
   public void propertyChange(PropertyChangeEvent evt) {
      super.propertyChange(evt);
      String prop = evt.getPropertyName();
      if (StateDefShape.START_PAGE.equals(prop)) {
         if (stateDefShape.isStartState()) {
            getFigure().setBackgroundColor(ColorConstants.yellow);
         } else {
            getFigure().setBackgroundColor(ColorConstants.green);
         }
      }
   }

   @Override
   public ConnectionAnchor getTargetConnectionAnchor(Request request) {
      // TODO implement sending back returnAnchor if appropriate
      return super.getTargetConnectionAnchor(request);
   }

   @Override
   public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
      if (connection.getModel() instanceof ReturnTransitionConnection) {
         if (returnAnchor == null) {
            returnAnchor = new RightAnchor(getFigure());
         }
         return returnAnchor;
      }
      return super.getTargetConnectionAnchor(connection);
   }

   @Override
   public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
      if (connection.getModel() instanceof ReturnTransitionConnection) {
         if (returnAnchor == null) {
            returnAnchor = new RightAnchor(getFigure());
         }
         return returnAnchor;
      }
      return super.getTargetConnectionAnchor(connection);
   }

   @Override
   public ConnectionAnchor getSourceConnectionAnchor(Request request) {
      // TODO implement sending back returnAnchor if appropriate
      return super.getSourceConnectionAnchor(request);
   }

}
