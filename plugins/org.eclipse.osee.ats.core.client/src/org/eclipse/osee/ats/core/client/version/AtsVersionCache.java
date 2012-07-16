/*
 * Created on Jul 16, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.client.version;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;

public class AtsVersionCache {

   public static AtsVersionCache instance = new AtsVersionCache();

   public Map<String, IAtsVersion> hridToVersion = new HashMap<String, IAtsVersion>(500);

   public IAtsVersion getVersion(IAtsTeamWorkflow teamWf) {
      return hridToVersion.get(teamWf.getHumanReadableId());
   }

   public boolean hasVersion(IAtsTeamWorkflow teamWf) {
      IAtsVersion version = getVersion(teamWf);
      return version != null && !version.equals(NullVersion.instance);
   }

   public IAtsVersion cache(IAtsTeamWorkflow teamWf, IAtsVersion version) {
      return hridToVersion.put(teamWf.getHumanReadableId(), version);
   }

   public void deCache(IAtsTeamWorkflow teamWf) {
      hridToVersion.remove(teamWf.getHumanReadableId());
   }

   public IAtsVersion cacheNull(IAtsTeamWorkflow teamWf) {
      return cache(teamWf, NullVersion.instance);
   }
}
