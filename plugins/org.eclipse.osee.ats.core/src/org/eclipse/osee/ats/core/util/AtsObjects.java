/*
 * Created on Feb 27, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.core.model.IAtsObject;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

public class AtsObjects {

   public static List<String> toGuids(Collection<? extends IAtsObject> atsObjects) {
      List<String> guids = new ArrayList<String>(atsObjects.size());
      for (IAtsObject atsObject : atsObjects) {
         guids.add(atsObject.getGuid());
      }
      return guids;
   }

   /**
    * getName() all atsObjects, else toString()
    */
   public static String commaArts(Collection<? extends Object> objects) {
      return toTextList(objects, ", ");
   }

   /**
    * getName() all atsObjects, else toString()
    */
   public static String semmicolonArts(Collection<? extends Object> objects) {
      return toTextList(objects, "; ");
   }

   /**
    * getName() all atsObjects, else toString()
    */
   public static String toString(String separator, Collection<? extends Object> objects) {
      return toTextList(objects, separator);
   }

   /**
    * getName() all atsObjects, else toString()
    */
   public static String toTextList(Collection<? extends Object> objects, String separator) {
      StringBuilder sb = new StringBuilder();
      for (Object obj : objects) {
         if (obj instanceof IAtsObject) {
            sb.append(((IAtsObject) obj).getName());
         } else {
            sb.append(obj.toString());
         }
         sb.append(separator);
      }
      if (sb.length() > separator.length()) {
         return sb.substring(0, sb.length() - separator.length());
      }
      return "";
   }

   public static Collection<String> getNames(Collection<? extends IAtsObject> atsObjects) {
      ArrayList<String> names = new ArrayList<String>();
      for (IAtsObject namedAtsObject : atsObjects) {
         names.add(namedAtsObject.getName());
      }
      return names;
   }

   /**
    * Recurses default hierarchy and collections children of parent that are of type class
    */
   @SuppressWarnings("unchecked")
   public static <A extends IAtsObject> void getChildrenOfType(IAtsObject parent, Collection<A> children, Class<A> clazz, boolean recurse) throws OseeCoreException {
      for (IAtsObject child : parent.getAtsChildren().getChildren()) {
         if (child.getClass().equals(clazz)) {
            children.add((A) child);
            if (recurse) {
               getChildrenOfType(child, children, clazz, recurse);
            }
         }
      }
   }

   /**
    * @return Set of type class that includes parent and children and will recurse children if true
    */
   @SuppressWarnings("unchecked")
   public static <A extends IAtsObject> Set<A> getChildrenAndThisOfTypeSet(IAtsObject parent, Class<A> clazz, boolean recurse) throws OseeCoreException {
      Set<A> thisAndChildren = new HashSet<A>();
      if (parent.getClass().equals(clazz)) {
         thisAndChildren.add((A) parent);
      }
      getChildrenOfTypeSet(parent, clazz, recurse);
      return thisAndChildren;
   }

   @SuppressWarnings("unchecked")
   public static <A extends IAtsObject> Set<A> getChildrenOfTypeSet(IAtsObject parent, Class<A> clazz, boolean recurse) throws OseeCoreException {
      Set<A> children = new HashSet<A>();
      for (IAtsObject child : parent.getAtsChildren().getChildren()) {
         if (child.getClass().equals(clazz)) {
            children.add((A) child);
            if (recurse) {
               getChildrenOfType(child, children, clazz, recurse);
            }
         }
      }
      return children;
   }

}
