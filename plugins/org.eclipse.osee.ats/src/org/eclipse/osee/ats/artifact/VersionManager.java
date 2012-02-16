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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.core.client.config.TeamDefinitionArtifact;
import org.eclipse.osee.ats.core.client.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.client.type.AtsRelationTypes;
import org.eclipse.osee.ats.core.client.util.AtsCacheManager;
import org.eclipse.osee.ats.core.client.version.VersionArtifact;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

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

   public static Collection<VersionArtifact> createVersions(XResultData resultData, SkynetTransaction transaction, TeamDefinitionArtifact teamDefHoldingVersions, Collection<String> newVersionNames) throws OseeCoreException {
      List<VersionArtifact> verArts = new ArrayList<VersionArtifact>();
      for (String newVer : newVersionNames) {
         if (!Strings.isValid(newVer)) {
            resultData.logError("Version name can't be blank");
         }
         for (Artifact verArt : teamDefHoldingVersions.getVersionsArtifacts()) {
            if (verArt.getName().equals(newVer)) {
               resultData.logError(String.format("Version [%s] already exists", newVer));
            }
         }
      }
      if (!resultData.isErrors()) {
         try {
            for (String newVer : newVersionNames) {
               Artifact ver = ArtifactTypeManager.addArtifact(AtsArtifactTypes.Version, AtsUtil.getAtsBranch(), newVer);
               teamDefHoldingVersions.addRelation(AtsRelationTypes.TeamDefinitionToVersion_Version, ver);
               if (transaction != null) {
                  ver.persist(transaction);
               }
               verArts.add((VersionArtifact) ver);
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
      return verArts;
   }

}
