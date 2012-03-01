/*
 * Created on Feb 13, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.core.model.IAtsChildren;
import org.eclipse.osee.ats.core.model.IAtsObject;

public class AtsChildren implements IAtsChildren {

   public List<IAtsObject> children = new ArrayList<IAtsObject>();

   @Override
   public Collection<IAtsObject> getChildren() {
      return children;
   }

   @Override
   public void addChild(IAtsObject child) {
      if (!children.contains(child)) {
         children.add(child);
      }
   }

   @Override
   public void removeChild(IAtsObject child) {
      children.remove(child);
   }

   @Override
   public void addChildren(Collection<IAtsObject> children) {
      for (IAtsObject child : children) {
         addChild(child);
      }
   }

   @Override
   public void removeChildren(Collection<IAtsObject> children) {
      for (IAtsObject child : children) {
         removeChild(child);
      }
   }

   @Override
   public String toString() {
      if (children.isEmpty()) {
         return "[]";
      }
      return String.format("\n%s\n", children);
   }

}
