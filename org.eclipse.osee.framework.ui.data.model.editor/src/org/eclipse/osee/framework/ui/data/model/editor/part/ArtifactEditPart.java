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
import org.eclipse.osee.framework.ui.data.model.editor.model.NodeModel;
import org.eclipse.osee.framework.ui.data.model.editor.model.RelationDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.helper.ContainerModel;
import org.eclipse.osee.framework.ui.data.model.editor.model.helper.ContainerModel.ContainerType;
import org.eclipse.osee.framework.ui.data.model.editor.utility.ODMConstants;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactEditPart extends DataTypeEditPart {

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
      List<ContainerModel> modelChildren = new ArrayList<ContainerModel>();
      modelChildren.add(new ContainerModel(getArtifactDataType(), ContainerType.INHERITED_ATTRIBUTES));
      modelChildren.add(new ContainerModel(getArtifactDataType(), ContainerType.LOCAL_ATTRIBUTES));
      modelChildren.add(new ContainerModel(getArtifactDataType(), ContainerType.INHERITED_RELATIONS));
      modelChildren.add(new ContainerModel(getArtifactDataType(), ContainerType.LOCAL_RELATIONS));
      return modelChildren;
   }

   protected void refreshVisuals() {
      super.refreshVisuals();
      ArtifactTypeFigure artifactTypeFigure = ((ArtifactTypeFigure) getFigure());
      artifactTypeFigure.setHeaderIcon(getArtifactDataType().getImage());
      ((Label) artifactTypeFigure.getNamespaceFigure()).setText(ODMConstants.getNamespace(getArtifactDataType()));
      ((Label) artifactTypeFigure.getNameFigure()).setText(getArtifactDataType().getName());

      artifactTypeFigure.getNamespaceFigure().setFont(null);
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
         //            //            ConnectionModel connectionModel = (ConnectionModel) request.getNewObject();
         //            //            if (link instanceof ReferenceView || link instanceof InheritanceView) {
         Command cmd =
               new CreateConnectionCommand((ConnectionModel) request.getNewObject(),
                     (ArtifactDataType) getHost().getModel());
         request.setStartCommand(cmd);
         return cmd;
         //            //            }
         //            /*
         //             * The disallow cursor will be shown If you return null.  If you return
         //             * UnexecutableCommand.INSTANCE, the disallow cursor will not appear.  This is
         //             * because since this is the first step of the Command it doesn't check to 
         //             * see if it's executable or not (which it most likely isn't).
         //             */
         //            return null;
      }

      protected Command getReconnectSourceCommand(ReconnectRequest request) {
         //            ConnectionModel connectionModel = (ConnectionModel) request.getConnectionEditPart().getModel();
         //            if (link instanceof InheritanceView || link instanceof ReferenceView) {
         //               return new ReconnectLinkCommand(link, (Node) getHost().getModel(), true);
         //            }
         return UnexecutableCommand.INSTANCE;
      }

      protected Command getReconnectTargetCommand(ReconnectRequest request) {
         ConnectionModel connectionModel = (ConnectionModel) request.getConnectionEditPart().getModel();
         //            if (link instanceof ReferenceView || link instanceof InheritanceView) return UnexecutableCommand.INSTANCE;
         return new ReconnectConnectionCommand(connectionModel, (NodeModel) getHost().getModel(), false);
      }
   }
}
