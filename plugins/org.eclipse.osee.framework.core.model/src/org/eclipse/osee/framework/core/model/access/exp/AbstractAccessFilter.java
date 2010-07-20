package org.eclipse.osee.framework.core.model.access.exp;

public abstract class AbstractAccessFilter implements IAccessFilter {

   @Override
   public int compareTo(IAccessFilter o) {
      int toReturn = -1;
      if (o.getRank() > this.getRank()) {
         toReturn = 1;
      } else if (this.getRank() == o.getRank()) {
         toReturn = 0;
      }
      return toReturn;
   }
}
