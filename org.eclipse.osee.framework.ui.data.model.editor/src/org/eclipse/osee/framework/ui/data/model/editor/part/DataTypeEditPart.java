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
package org.eclipse.osee.framework.ui.data.model.editor.part;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.osee.framework.ui.data.model.editor.command.ChangeNameCommand;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataType;

/**
 * @author Roberto E. Escobar
 */
public abstract class DataTypeEditPart extends NodeModelEditPart {

   public DataTypeEditPart(DataType model) {
      super(model);
   }

   public void activate() {
      super.activate();
      getDataType().addListener(modelListener);
   }

   protected DirectEditPolicy createDirectEditPolicy() {
      return new DirectEditPolicy() {
         protected Command getDirectEditCommand(DirectEditRequest request) {
            return new ChangeNameCommand(getDataType(), (String) request.getCellEditor().getValue());
         }

         protected void showCurrentEditValue(DirectEditRequest request) {
            IFigure fig = getDirectEditFigure();
            if (fig instanceof Label) {
               ((Label) fig).setText((String) request.getCellEditor().getValue());
               fig.getUpdateManager().performUpdate();
            }
         }
      };
   }

   protected void createEditPolicies() {
      super.createEditPolicies();
      //      installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new GraphicalNodeEditPolicy() {
      //         protected Command getConnectionCompleteCommand(CreateConnectionRequest request) {
      //            Link link = (Link) request.getNewObject();
      //            if (link instanceof ReferenceView || link instanceof InheritanceView) return null;
      //            LinkCreationCommand cmd = (LinkCreationCommand) request.getStartCommand();
      //            cmd.setTarget((Node) getHost().getModel());
      //            return cmd;
      //         }
      //
      //         protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
      //            return null;
      //         }
      //
      //         protected Command getReconnectSourceCommand(ReconnectRequest request) {
      //            return UnexecutableCommand.INSTANCE;
      //         }
      //
      //         protected Command getReconnectTargetCommand(ReconnectRequest request) {
      //            Link link = (Link) request.getConnectionEditPart().getModel();
      //            if (link instanceof ReferenceView || link instanceof InheritanceView) return UnexecutableCommand.INSTANCE;
      //            return new ReconnectLinkCommand(link, (Node) getHost().getModel(), false);
      //         }
      //      });
   }

   public void deactivate() {
      getDataType().removeListener(modelListener);
      super.deactivate();
   }

   protected DataType getDataType() {
      return (DataType) getModel();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.data.model.editor.part.BaseEditPart#handleModelChanged(java.lang.Object)
    */
   @Override
   protected void handleModelEvent(Object msg) {
      refreshVisuals();
      super.handleModelEvent(msg);
   }
}
