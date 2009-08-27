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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.editpolicies.LayoutEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.ReconnectRequest;
import org.eclipse.osee.framework.ui.data.model.editor.command.CreateAttributeCommand;
import org.eclipse.osee.framework.ui.data.model.editor.command.CreateConnectionCommand;
import org.eclipse.osee.framework.ui.data.model.editor.command.CreateRelationCommand;
import org.eclipse.osee.framework.ui.data.model.editor.command.ReconnectConnectionCommand;
import org.eclipse.osee.framework.ui.data.model.editor.figure.ArtifactTypeFigure;
import org.eclipse.osee.framework.ui.data.model.editor.figure.SelectableLabel;
import org.eclipse.osee.framework.ui.data.model.editor.model.ArtifactDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.AttributeDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.ConnectionModel;
import org.eclipse.osee.framework.ui.data.model.editor.model.InheritanceLinkModel;
import org.eclipse.osee.framework.ui.data.model.editor.model.RelationDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.RelationLinkModel;
import org.eclipse.osee.framework.ui.data.model.editor.model.helper.ContainerModel;
import org.eclipse.osee.framework.ui.data.model.editor.model.helper.ContainerModel.ContainerType;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactEditPart extends DataTypeEditPart {

   @SuppressWarnings("unchecked")
   private List children;

   public ArtifactEditPart(Object model) {
      super((ArtifactDataType) model);
   }

   protected void createEditPolicies() {
      super.createEditPolicies();
      installEditPolicy(EditPolicy.LAYOUT_ROLE, new ArtifactLayoutEditPolicy());
      installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new ArtifactGraphicalNodeEditPolicy());
   }

   protected IFigure createFigure() {
      return new ArtifactTypeFigure(new Label(), new SelectableLabel());
   }

   public IFigure getContentPane() {
      return ((ArtifactTypeFigure) getFigure()).getContentPane();
   }

   protected IFigure getDirectEditFigure() {
      return ((ArtifactTypeFigure) getFigure()).getNameFigure();
   }

   private ArtifactDataType getArtifactDataType() {
      return (ArtifactDataType) getDataType();
   }

   public boolean canDeleteFromDiagram() {
      return true;
   }

   @SuppressWarnings("unchecked")
   protected List getModelChildren() {
      ArtifactDataType model = getArtifactDataType();
      if (children == null) {
         children = new ArrayList();
         children.add(new ContainerModel(model, ContainerType.INHERITED_ATTRIBUTES));
         children.add(new ContainerModel(model, ContainerType.LOCAL_ATTRIBUTES));
         children.add(new ContainerModel(model, ContainerType.INHERITED_RELATIONS));
         children.add(new ContainerModel(model, ContainerType.LOCAL_RELATIONS));
      }
//      checkInheritance();
      return children;
   }

//   private void checkInheritance() {
//      ArtifactDataType model = getArtifactDataType();
//
//      for (ArtifactDataType key : toRemove) {
//         InheritanceLinkModel link = inheritanceMap.remove(key);
//         if (link != null) {
//            children.remove(link);
//         }
//      }
//   }

   protected void refreshVisuals() {
      super.refreshVisuals();
      ArtifactTypeFigure artifactTypeFigure = ((ArtifactTypeFigure) getFigure());
      artifactTypeFigure.setHeaderIcon(getArtifactDataType().getImage());
      ((Label) artifactTypeFigure.getNameFigure()).setText(getArtifactDataType().getName());

      artifactTypeFigure.getNameFigure().setFont(null);

      getFigure().setBackgroundColor(ColorConstants.white);
      super.refreshChildren();
   }

   protected void handleModelEvent(Object msg) {
      refreshVisuals();
   }

   private final class ArtifactLayoutEditPolicy extends LayoutEditPolicy {
      protected EditPolicy createChildEditPolicy(EditPart child) {
         return null;
      }

      protected Command getCreateCommand(CreateRequest request) {
         if (request.getNewObject() instanceof AttributeDataType) {
            return new CreateAttributeCommand((AttributeDataType) request.getNewObject(),
                  ((ArtifactEditPart) getHost()).getArtifactDataType());
         } else if (request.getNewObject() instanceof RelationDataType) {
            return new CreateRelationCommand(((RelationDataType) request.getNewObject()),
                  ((ArtifactEditPart) getHost()).getArtifactDataType());
         }
         return UnexecutableCommand.INSTANCE;
      }

      protected Command getDeleteDependantCommand(Request request) {
         return null;
      }

      protected Command getMoveChildrenCommand(Request request) {
         return UnexecutableCommand.INSTANCE;
      }
   }

   private final class ArtifactGraphicalNodeEditPolicy extends GraphicalNodeEditPolicy {
      protected Command getConnectionCompleteCommand(CreateConnectionRequest request) {
         CreateConnectionCommand cmd = (CreateConnectionCommand) request.getStartCommand();
         cmd.setTarget((ArtifactDataType) getHost().getModel());
         return cmd;
      }

      protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
         ConnectionModel connectionModel = (ConnectionModel) request.getNewObject();
         if (connectionModel instanceof RelationLinkModel || connectionModel instanceof InheritanceLinkModel) {
            ArtifactDataType source = (ArtifactDataType) getHost().getModel();
            Command cmd = new CreateConnectionCommand(connectionModel, source);
            request.setStartCommand(cmd);
            return cmd;
         }
         return null;
      }

      @SuppressWarnings("unchecked")
      protected Command getReconnectSourceCommand(ReconnectRequest request) {
         ConnectionModel<ArtifactDataType> connectionModel =
               (ConnectionModel<ArtifactDataType>) request.getConnectionEditPart().getModel();
         if (connectionModel instanceof RelationLinkModel || connectionModel instanceof InheritanceLinkModel) {
            ArtifactDataType source = (ArtifactDataType) getHost().getModel();
            ReconnectConnectionCommand cmd = new ReconnectConnectionCommand(connectionModel);
            cmd.setNewSource(source);
            return cmd;
         }
         return UnexecutableCommand.INSTANCE;
      }

      @SuppressWarnings("unchecked")
      protected Command getReconnectTargetCommand(ReconnectRequest request) {
         ConnectionModel<ArtifactDataType> connectionModel =
               (ConnectionModel<ArtifactDataType>) request.getConnectionEditPart().getModel();
         if (connectionModel instanceof RelationLinkModel || connectionModel instanceof InheritanceLinkModel) {
            ArtifactDataType target = (ArtifactDataType) getHost().getModel();
            ReconnectConnectionCommand cmd = new ReconnectConnectionCommand(connectionModel);
            cmd.setNewTarget(target);
            return cmd;
         }
         return UnexecutableCommand.INSTANCE;
      }
   }
}
