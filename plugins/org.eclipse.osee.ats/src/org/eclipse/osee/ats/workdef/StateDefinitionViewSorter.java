package org.eclipse.osee.ats.workdef;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * @author Donald G. Dunne
 */
public class StateDefinitionViewSorter extends ViewerSorter {

   public StateDefinitionViewSorter() {
      super();
   }

   @Override
   public int compare(Viewer viewer, Object e1, Object e2) {
      StateDefinition def1 = (StateDefinition) e1;
      StateDefinition def2 = (StateDefinition) e2;
      if (def1.getOrdinal() == def2.getOrdinal()) {
         return compareByName(def1, def2);
      } else if (def1.getOrdinal() < def2.getOrdinal()) {
         return -1;
      } else {
         return 1;
      }
   }

   @SuppressWarnings("unchecked")
   private int compareByName(StateDefinition def1, StateDefinition def2) {
      return getComparator().compare(def1.getName(), def2.getName());
   }
}
