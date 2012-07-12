/*
 * Created on Jun 19, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.client.config;

import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.core.client.config.store.ActionableItemArtifactStore;
import org.eclipse.osee.ats.core.client.config.store.TeamDefinitionArtifactStore;
import org.eclipse.osee.ats.core.client.config.store.VersionArtifactStore;
import org.eclipse.osee.ats.core.config.AtsConfigCache;
import org.eclipse.osee.ats.core.model.IAtsActionableItem;
import org.eclipse.osee.ats.core.model.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.model.IAtsVersion;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class AtsConfigManagerClient {

   public static void cacheConfigArtifact(Artifact artifact) throws OseeCoreException {
      // cache
      if (artifact.isOfType(AtsArtifactTypes.TeamDefinition)) {
         TeamDefinitionArtifactStore store = new TeamDefinitionArtifactStore(artifact);
         IAtsTeamDefinition teamDef = store.getTeamDefinition();

         for (String staticId : artifact.getAttributesToStringList(CoreAttributeTypes.StaticId)) {
            AtsConfigCache.cacheByTag(staticId, teamDef);
         }
      }
      if (artifact.isOfType(AtsArtifactTypes.ActionableItem)) {
         ActionableItemArtifactStore store = new ActionableItemArtifactStore(artifact);
         IAtsActionableItem ai = store.getActionableItem();

         for (String staticId : artifact.getAttributesToStringList(CoreAttributeTypes.StaticId)) {
            AtsConfigCache.cacheByTag(staticId, ai);
         }
      }
      if (artifact.isOfType(AtsArtifactTypes.Version)) {
         VersionArtifactStore store = new VersionArtifactStore(artifact);
         IAtsVersion version = store.getVersion();

         for (String staticId : artifact.getAttributesToStringList(CoreAttributeTypes.StaticId)) {
            AtsConfigCache.cacheByTag(staticId, version);
         }
      }
   }

}
