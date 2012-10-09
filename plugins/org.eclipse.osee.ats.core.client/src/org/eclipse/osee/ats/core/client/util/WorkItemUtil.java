/*
 * Created on Aug 3, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.client.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

public class WorkItemUtil {

   public static Artifact get(IAtsWorkItem workItem) throws OseeCoreException {
      if (workItem instanceof Artifact) {
         return (Artifact) workItem;
      }
      Artifact artifact =
         ArtifactQuery.getArtifactFromId(workItem.getGuid(), AtsUtilCore.getAtsBranchToken(),
            DeletionFlag.EXCLUDE_DELETED);
      return artifact;
   }

   @SuppressWarnings("unchecked")
   public static <A extends Artifact> A get(IAtsWorkItem workItem, Class<?> clazz) throws OseeCoreException {
      Artifact artifact = get(workItem);
      if (clazz.isInstance(artifact)) {
         return (A) artifact;
      }
      return null;
   }

   @SuppressWarnings("unchecked")
   public static <A extends Artifact> List<A> get(Collection<? extends IAtsWorkItem> workItems, Class<?> clazz) throws OseeCoreException {
      List<A> arts = new ArrayList<A>();
      for (IAtsWorkItem workItem : workItems) {
         Artifact artifact = get(workItem, clazz);
         if (artifact != null) {
            arts.add((A) artifact);
         }
      }
      return arts;
   }

   public static Collection<? extends IAtsWorkItem> getWorkItems(Collection<Artifact> arts) {
      return Collections.castMatching(IAtsWorkItem.class, arts);
   }
}
