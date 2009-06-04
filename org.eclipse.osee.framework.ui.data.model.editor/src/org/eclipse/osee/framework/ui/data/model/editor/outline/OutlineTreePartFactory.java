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
package org.eclipse.osee.framework.ui.data.model.editor.outline;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.osee.framework.ui.data.model.editor.part.StringEditPart;

/**
 * @author Roberto E. Escobar
 */
public class OutlineTreePartFactory implements EditPartFactory {

   public OutlineTreePartFactory() {
      super();
   }

   public EditPart createEditPart(EditPart context, Object model) {
      EditPart toReturn = null;
      if (model instanceof String) {
         toReturn = new StringEditPart((String) model);
      }
      //      if (model instanceof Diagram) {
      //         return new DiagramTreeEditPart((Diagram) model);
      //      } else if (model instanceof EPackage) {
      //         return new PackageTreeEditPart((EPackage) model);
      //      } else if (model instanceof EClassifier) {
      //         return new ClassifierTreeEditPart((EClassifier) model);
      //      } else if (model instanceof EReference) {
      //         return new ReferenceTreeEditPart((EReference) model);
      //      } else if (model instanceof InheritanceModel) {
      //         return new InheritanceTreeEditPart((InheritanceModel) model);
      //      }
      return toReturn;
   }
}
