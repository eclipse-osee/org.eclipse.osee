/*
 * Created on Mar 7, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.artifact;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.config.AtsCacheManager;
import org.eclipse.osee.ats.util.AtsArtifactTypes;

public class VersionManager {

   public static Set<VersionArtifact> getVersions(Collection<String> teamDefNames) {
      Set<VersionArtifact> versions = new HashSet<VersionArtifact>();
      for (String versionName : teamDefNames) {
         versions.add(getSoleVersion(versionName));
      }
      return versions;
   }

   /**
    * Refrain from using this method as Version Artifact names can be changed by the user.
    */
   public static VersionArtifact getSoleVersion(String name) {
      return (VersionArtifact) AtsCacheManager.getArtifactsByName(AtsArtifactTypes.Version, name).iterator().next();
   }

}
