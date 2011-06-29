/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.artifact;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.core.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.util.AtsCacheManager;
import org.eclipse.osee.ats.core.version.VersionArtifact;

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
