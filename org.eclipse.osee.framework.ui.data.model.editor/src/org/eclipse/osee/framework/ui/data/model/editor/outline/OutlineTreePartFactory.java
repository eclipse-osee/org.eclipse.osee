/*
 * Created on Feb 23, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
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
