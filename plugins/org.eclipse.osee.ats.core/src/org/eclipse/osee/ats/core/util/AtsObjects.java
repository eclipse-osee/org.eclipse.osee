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
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * @author Donald G. Dunne
 */
public class AtsObjects {

   public static String toIdsString(String separator, Collection<? extends IAtsObject> atsObjects) {
      return Collections.toString(separator, toIds(atsObjects));
   }

   public static List<Long> toIds(Collection<? extends IAtsObject> atsObjects) {
      List<Long> ids = new ArrayList<>(atsObjects.size());
      for (IAtsObject atsObject : atsObjects) {
         ids.add(atsObject.getId());
      }
      return ids;
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

   public static Collection<ArtifactId> getArtifacts(Collection<?> objects) {
      List<ArtifactId> artifacts = new LinkedList<>();
      for (Object object : objects) {
         if (object instanceof ArtifactId) {
            artifacts.add((ArtifactId) object);
         } else if (object instanceof IAtsObject) {
            artifacts.add(((IAtsObject) object).getStoreObject());
         }
      }
      return artifacts;
   }

   /**
    * Return collection of TeamWfs. If Task or Review, return nothing. If Action, return children TeamWfs.
    */
   public static Collection<ArtifactId> getTeamWfArtifacts(Collection<?> objects, AtsApi atsApi) {
      List<ArtifactId> artifacts = new LinkedList<>();
      for (Object object : objects) {
         if (object instanceof ArtifactToken) {
            ArtifactToken artId = (ArtifactToken) object;
            if (atsApi.getStoreService().isOfType(artId, AtsArtifactTypes.Action)) {
               artifacts.addAll(AtsObjects.getArtifacts(
                  atsApi.getWorkItemService().getTeams(atsApi.getWorkItemFactory().getAction(artId))));
            }
         }
         if (object instanceof IAtsTeamWorkflow) {
            artifacts.add(((IAtsTeamWorkflow) object).getStoreObject());
         }
      }
      return artifacts;
   }

   @SuppressWarnings("unchecked")
   public static <T> Collection<T> getActive(Collection<? extends IAtsConfigObject> objects) {
      List<T> active = new LinkedList<>();
      for (IAtsConfigObject obj : objects) {
         if (obj.isActive()) {
            active.add((T) obj);
         }
      }
      return active;
   }

   public static List<String> toIdStrings(Collection<? extends IAtsConfigObject> objects) {
      List<String> ids = new LinkedList<>();
      for (IAtsConfigObject obj : objects) {
         ids.add(obj.getIdString());
      }
      return ids;
   }

   public static List<String> toIdStringsFromLong(Collection<Long> ids) {
      List<String> results = new LinkedList<>();
      for (Long id : ids) {
         results.add(id.toString());
      }
      return results;
   }

   public static boolean isAtsWorkItemOrAction(Object element) {
      return element instanceof IAtsAction || element instanceof IAtsWorkItem;
   }

}
