package org.eclipse.osee.framework.ui.data.model.editor.part;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.osee.framework.ui.data.model.editor.command.ChangeNameCommand;
import org.eclipse.osee.framework.ui.data.model.editor.figure.SelectableLabel;
import org.eclipse.osee.framework.ui.data.model.editor.model.RelationDataType;
import org.eclipse.osee.framework.ui.data.model.editor.part.ArtifactEditPart.ArtifactInternalsModel.InternalEnum;
import org.eclipse.osee.framework.ui.data.model.editor.utility.ODMConstants;
import org.eclipse.osee.framework.ui.data.model.editor.utility.ODMImages;

/**
 * @author Roberto E. Escobar
 */
public class RelationEditPart extends BaseEditPart {

   public RelationEditPart(Object model) {
      super((RelationDataType) model);
   }

   protected DirectEditPolicy createDirectEditPolicy() {
      return new DirectEditPolicy() {
         protected Command getDirectEditCommand(DirectEditRequest request) {
            return new ChangeNameCommand(getRelationDataType(), (String) request.getCellEditor().getValue());
         }

         protected void showCurrentEditValue(DirectEditRequest request) {
            ((Label) getFigure()).setText((String) request.getCellEditor().getValue());
            getFigure().getUpdateManager().performUpdate();
         }
      };
   }

   protected IFigure createFigure() {
      SelectableLabel fig = new SelectableLabel();
      return fig;
   }

   protected String getDirectEditText() {
      return getRelationDataType().getName();
   }

   private RelationDataType getRelationDataType() {
      return (RelationDataType) getModel();
   }

   protected void refreshVisuals() {
      SelectableLabel labelFigure = (SelectableLabel) getFigure();
      String text = ODMConstants.getDataTypeText(getRelationDataType());
      labelFigure.setText(text);
      labelFigure.setIcon(ODMImages.getImage(ODMImages.RELATION_ENTRY));
      labelFigure.setSelectable(true);

      InternalArtifactEditPart internalArtifactEditPart = ((InternalArtifactEditPart) getParent());
      InternalEnum value = internalArtifactEditPart.getInternalType();
      if (value != null && value == InternalEnum.INHERITED_RELATIONS) {
         labelFigure.setBackgroundColor(ColorConstants.blue);
         labelFigure.setIcon(ODMImages.getImage(ODMImages.INHERITANCE));
         labelFigure.setSelectable(false);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.data.model.editor.part.BaseEditPart#handleModelChanged(java.lang.Object)
    */
   @Override
   protected void handleModelEvent(Object msg) {
      refreshVisuals();
   }
}
