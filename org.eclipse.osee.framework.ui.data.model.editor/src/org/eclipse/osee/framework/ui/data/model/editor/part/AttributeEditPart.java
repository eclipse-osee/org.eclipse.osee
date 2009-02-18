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

import org.eclipse.draw2d.Label;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.osee.framework.ui.data.model.editor.command.ChangeNameCommand;
import org.eclipse.osee.framework.ui.data.model.editor.model.AttributeDataType;
import org.eclipse.osee.framework.ui.data.model.editor.utility.ODMConstants;
import org.eclipse.osee.framework.ui.data.model.editor.utility.ODMImages;

/**
 * @author Roberto E. Escobar
 */
public class AttributeEditPart extends BaseEditPart {

   public AttributeEditPart(Object model) {
      super((AttributeDataType) model);
   }

   protected DirectEditPolicy createDirectEditPolicy() {
      return new DirectEditPolicy() {
         protected Command getDirectEditCommand(DirectEditRequest request) {
            return new ChangeNameCommand(getAttributeDataType(), (String) request.getCellEditor().getValue());
         }

         protected void showCurrentEditValue(DirectEditRequest request) {
            ((Label) getFigure()).setText((String) request.getCellEditor().getValue());
            getFigure().getUpdateManager().performUpdate();
         }
      };
   }

   private AttributeDataType getAttributeDataType() {
      return (AttributeDataType) getModel();
   }

   protected String getDirectEditText() {
      return getAttributeDataType().getName();
   }

   protected void handleModelEvent(Object msg) {
      refreshVisuals();
   }

   protected void refreshVisuals() {
      Label labelFigure = (Label) getFigure();
      String displayText = ODMConstants.getDataTypeText(getAttributeDataType());
      labelFigure.setText(displayText);
      labelFigure.setIcon(ODMImages.getImage(ODMImages.ATTRIBUTE_ENTRY));
   }

}
