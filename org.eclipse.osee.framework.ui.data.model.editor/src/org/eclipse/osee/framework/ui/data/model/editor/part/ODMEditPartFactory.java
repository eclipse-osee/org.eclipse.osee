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

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.osee.framework.ui.data.model.editor.ODMEditor;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.ODMGraph;

/**
 * @author Roberto E. Escobar
 */
public class ODMEditPartFactory implements EditPartFactory {
   private ODMEditor editor;

   public ODMEditPartFactory(ODMEditor editor) {
      this.editor = editor;
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart, java.lang.Object)
    */
   public EditPart createEditPart(EditPart context, Object model) {
      EditPart editPart = null;
      if (model instanceof String) {
         editPart = new LabelEditPart((String) model);
      } else if (model instanceof ODMGraph) {
         editPart = new GraphEditPart(editor.getViewer());
      } else if (model instanceof DataType) {
         editPart = new DataTypeEditPart(editor);
      }
      if (editPart == null) {
         throw new RuntimeException(String.format("Error no EditPart defined for: [%s]", model.getClass().getName()));
      } else {
         editPart.setModel(model);
      }
      return editPart;
   }
}