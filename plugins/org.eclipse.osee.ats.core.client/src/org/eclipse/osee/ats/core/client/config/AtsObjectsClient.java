/*
 * Created on Jun 7, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.client.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.core.client.util.AtsUtilCore;
import org.eclipse.osee.ats.core.config.AtsConfigCache;
import org.eclipse.osee.ats.core.model.IAtsConfigObject;
import org.eclipse.osee.ats.core.model.IAtsObject;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class AtsObjectsClient {

   public static Collection<Artifact> getArtifacts(Collection<? extends IAtsObject> atsObjects) throws OseeCoreException {
      List<String> guids = new ArrayList<String>();
      for (IAtsObject atsObject : atsObjects) {
         guids.add(atsObject.getGuid());
      }
      return ArtifactQuery.getArtifactListFromIds(guids, AtsUtilCore.getAtsBranchToken());
   }

   public static <A extends IAtsConfigObject> Collection<A> getConfigObjects(Collection<? extends Artifact> artifacts, Class<A> clazz) {
      List<A> objects = new ArrayList<A>();
      for (Artifact art : artifacts) {
         objects.addAll(AtsConfigCache.getByTag(art.getGuid(), clazz));
      }
      return objects;
   }

   public static Artifact getSoleArtifact(IAtsObject artifact) throws OseeCoreException {
      return ArtifactQuery.getArtifactFromId(artifact.getGuid(), AtsUtilCore.getAtsBranchToken());
   }
}
