/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * @author Donald G. Dunne
 */
public class AtsObjects {

   public static String toUuidsString(String separator, Collection<? extends IAtsObject> atsObjects) {
      return Collections.toString(separator, toUuids(atsObjects));
   }

   public static List<Long> toUuids(Collection<? extends IAtsObject> atsObjects) {
      List<Long> uuids = new ArrayList<>(atsObjects.size());
      for (IAtsObject atsObject : atsObjects) {
         uuids.add(atsObject.getUuid());
      }
      return uuids;
   }

   public static List<String> toGuids(Collection<? extends IAtsObject> atsObjects) {
      List<String> guids = new ArrayList<>(atsObjects.size());
      for (IAtsObject atsObject : atsObjects) {
         guids.add(AtsUtilCore.getGuid(atsObject));
      }
      return guids;
   }

   public static List<String> toAtsIds(Collection<? extends IAtsWorkItem> workItem) {
      List<String> guids = new ArrayList<>(workItem.size());
      for (IAtsWorkItem atsObject : workItem) {
         guids.add(atsObject.getAtsId());
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
      ArrayList<String> names = new ArrayList<>();
      for (IAtsObject namedAtsObject : atsObjects) {
         names.add(namedAtsObject.getName());
      }
      return names;
   }

   public static String toAtsIdsFromActions(Collection<IAtsAction> actions) {
      List<String> guids = new ArrayList<>(actions.size());
      for (IAtsAction action : actions) {
         guids.add(action.getAtsId());
      }
      return Collections.toString(", ", guids);
   }

   /**
    * @param semicolon delimited list of uuids
    */
   public static Collection<Long> uuidsToLong(String separator, String uuidsList) {
      List<Long> uuids = new ArrayList<>();
      for (String uuid : uuidsList.split(separator)) {
         uuids.add(Long.valueOf(uuid));
      }
      return uuids;
   }

}
