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

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.osee.framework.ui.data.model.editor.figure.SelectableLabel;
import org.eclipse.osee.framework.ui.data.model.editor.model.AttributeDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.helper.ContainerModel.ContainerType;
import org.eclipse.osee.framework.ui.data.model.editor.utility.ODMConstants;
import org.eclipse.osee.framework.ui.data.model.editor.utility.ODMImages;

/**
 * @author Roberto E. Escobar
 */
public class AttributeEditPart extends ComponentEditPart {

   public AttributeEditPart(Object model) {
      super((AttributeDataType) model);
   }

   @Override
   protected boolean isInherited() {
      ContainerType value = getContainerType();
      return value != null && value == ContainerType.INHERITED_ATTRIBUTES;
   }

   protected void refreshVisuals() {
      SelectableLabel labelFigure = (SelectableLabel) getFigure();
      String displayText = ODMConstants.getDataTypeText(getModelAsDataType());
      labelFigure.setText(displayText);
      labelFigure.setIcon(ODMImages.getImage(ODMImages.LOCAL_ATTRIBUTE));
      labelFigure.setSelectable(true);

      if (isInherited()) {
         labelFigure.setBackgroundColor(ColorConstants.tooltipBackground);
         labelFigure.setIcon(ODMImages.getImage(ODMImages.INHERITED_ATTRIBUTE));
         labelFigure.setSelectable(false);
      }
   }
}
