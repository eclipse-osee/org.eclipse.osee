/*
 * Created on Feb 27, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.core.model.IAtsObject;

/**
 * @author Donald G. Dunne
 */
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
   public static String toString(String separator, Collection<? extends Object> objects) {
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

}
