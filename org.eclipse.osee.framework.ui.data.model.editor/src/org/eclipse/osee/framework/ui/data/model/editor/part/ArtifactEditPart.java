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
import org.eclipse.gef.editpolicies.LayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.osee.framework.ui.data.model.editor.command.CreateAttributeCommand;
import org.eclipse.osee.framework.ui.data.model.editor.command.CreateRelationCommand;
import org.eclipse.osee.framework.ui.data.model.editor.figure.ArtifactTypeFigure;
import org.eclipse.osee.framework.ui.data.model.editor.figure.SelectableLabel;
import org.eclipse.osee.framework.ui.data.model.editor.model.ArtifactDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.AttributeDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.RelationDataType;
import org.eclipse.osee.framework.ui.data.model.editor.part.ArtifactEditPart.ArtifactInternalsModel.ComponentType;
import org.eclipse.osee.framework.ui.data.model.editor.utility.ODMConstants;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactEditPart extends DataTypeEditPart {

   private List modelChildren;

   public ArtifactEditPart(Object model) {
      super((ArtifactDataType) model);
   }

   protected void createEditPolicies() {
      super.createEditPolicies();
      installEditPolicy(EditPolicy.LAYOUT_ROLE, new LayoutEditPolicy() {
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
      });

      //      installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new GraphicalNodeEditPolicy() {
      //         protected Command getConnectionCompleteCommand(CreateConnectionRequest request) {
      //            LinkCreationCommand cmd = (LinkCreationCommand) request.getStartCommand();
      //            cmd.setTarget((NamedElementView) getHost().getModel());
      //            return cmd;
      //         }
      //
      //         protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
      //            Link link = (Link) request.getNewObject();
      //            if (link instanceof ReferenceView || link instanceof InheritanceView) {
      //               Command cmd =
      //                     new LinkCreationCommand((Link) request.getNewObject(), (NamedElementView) getHost().getModel());
      //               request.setStartCommand(cmd);
      //               return cmd;
      //            }
      //            /*
      //             * The disallow cursor will be shown IFF you return null.  If you return
      //             * UnexecutableCommand.INSTANCE, the disallow cursor will not appear.  This is
      //             * because since this is the first step of the Command it doesn't check to 
      //             * see if it's executable or not (which it most likely isn't).
      //             */
      //            return null;
      //         }
      //
      //         protected Command getReconnectSourceCommand(ReconnectRequest request) {
      //            Link link = (Link) request.getConnectionEditPart().getModel();
      //            if (link instanceof InheritanceView || link instanceof ReferenceView) return new ReconnectLinkCommand(link,
      //                  (Node) getHost().getModel(), true);
      //            return UnexecutableCommand.INSTANCE;
      //         }
      //
      //         protected Command getReconnectTargetCommand(ReconnectRequest request) {
      //            return new ReconnectLinkCommand((Link) request.getConnectionEditPart().getModel(),
      //                  (Node) getHost().getModel(), false);
      //         }
      //      });
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

   protected List getModelChildren() {
      if (modelChildren == null) {
         modelChildren = new ArrayList();
         modelChildren.add(new ArtifactInternalsModel(getArtifactDataType(), ComponentType.INHERITED_ATTRIBUTES));
         modelChildren.add(new ArtifactInternalsModel(getArtifactDataType(), ComponentType.LOCAL_ATTRIBUTES));
         modelChildren.add(new ArtifactInternalsModel(getArtifactDataType(), ComponentType.INHERITED_RELATIONS));
         modelChildren.add(new ArtifactInternalsModel(getArtifactDataType(), ComponentType.LOCAL_RELATIONS));
      }
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
   }
   public static final class ArtifactInternalsModel {
      private ArtifactDataType artifactDataType;
      private ComponentType internals;

      public static enum ComponentType {
         INHERITED_ATTRIBUTES, INHERITED_RELATIONS, LOCAL_RELATIONS, LOCAL_ATTRIBUTES;
      }

      public ArtifactInternalsModel(ArtifactDataType theClass, ComponentType internals) {
         this.artifactDataType = theClass;
         this.internals = internals;
      }

      public List getChildren() {
         List<? extends DataType> children = new ArrayList<DataType>();
         switch (internals) {
            case INHERITED_ATTRIBUTES:
               children = artifactDataType.getInheritedAttributes();
               break;
            case LOCAL_ATTRIBUTES:
               children = artifactDataType.getLocalAttributes();
               break;
            case INHERITED_RELATIONS:
               children = artifactDataType.getInheritedRelations();
               break;
            case LOCAL_RELATIONS:
               children = artifactDataType.getLocalRelations();
               break;
            default:
               break;
         }
         return children;
      }

      public ComponentType getInternalDescription() {
         return internals;
      }
   }

   protected void handleModelEvent(Object msg) {
      refreshVisuals();
   }
}
