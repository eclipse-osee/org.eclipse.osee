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
import org.eclipse.osee.framework.ui.data.model.editor.model.DataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.helper.ContainerModel.ContainerType;

/**
 * @author Roberto E. Escobar
 */
public abstract class ComponentEditPart extends BaseEditPart {

   public ComponentEditPart(DataType dataType) {
      super(dataType);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.data.model.editor.part.BaseEditPart#createDirectEditPolicy()
    */
   @Override
   protected DirectEditPolicy createDirectEditPolicy() {
      return new DirectEditPolicy() {
         protected Command getDirectEditCommand(DirectEditRequest request) {
            return new ChangeNameCommand(getModelAsDataType(), (String) request.getCellEditor().getValue());
         }

         protected void showCurrentEditValue(DirectEditRequest request) {
            ((Label) getFigure()).setText((String) request.getCellEditor().getValue());
            getFigure().getUpdateManager().performUpdate();
         }
      };
   }

   protected ContainerType getContainerType() {
      ContainerEditPart internalArtifactEditPart = ((ContainerEditPart) getParent());
      return internalArtifactEditPart.getContainerType();
   }

   protected DataType getModelAsDataType() {
      return (DataType) getModel();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.data.model.editor.part.BaseEditPart#handleModelEvent(java.lang.Object)
    */
   @Override
   protected void handleModelEvent(Object object) {
      refreshVisuals();
   }

   protected String getDirectEditText() {
      return getModelAsDataType().getName();
   }

   @Override
   protected void performDirectEdit() {
      if (!isInherited()) {
         super.performDirectEdit();
      }
   }

   protected abstract boolean isInherited();
}
