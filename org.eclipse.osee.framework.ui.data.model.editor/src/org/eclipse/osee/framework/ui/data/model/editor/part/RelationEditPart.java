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
import org.eclipse.osee.framework.ui.data.model.editor.model.RelationDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.helper.ContainerModel.ContainerType;
import org.eclipse.osee.framework.ui.data.model.editor.utility.ODMConstants;
import org.eclipse.osee.framework.ui.data.model.editor.utility.ODMImages;

/**
 * @author Roberto E. Escobar
 */
public class RelationEditPart extends ComponentEditPart {

   public RelationEditPart(Object model) {
      super((RelationDataType) model);
   }

   protected boolean isInherited() {
      ContainerType value = getContainerType();
      return value != null && value == ContainerType.INHERITED_RELATIONS;
   }

   protected void refreshVisuals() {
      SelectableLabel labelFigure = (SelectableLabel) getFigure();
      String text = ODMConstants.getDataTypeText(getModelAsDataType());
      labelFigure.setText(text);
      labelFigure.setIcon(ODMImages.getImage(ODMImages.LOCAL_RELATION));
      labelFigure.setSelectable(true);

      if (isInherited()) {
         labelFigure.setBackgroundColor(ColorConstants.blue);
         labelFigure.setIcon(ODMImages.getImage(ODMImages.INHERITED_RELATION));
         labelFigure.setSelectable(false);
      }
   }
}
