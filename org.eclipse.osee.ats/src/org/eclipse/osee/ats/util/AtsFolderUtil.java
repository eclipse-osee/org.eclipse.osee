/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.util;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.StaticIdManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * @author Donald G. Dunne
 */
public class AtsFolderUtil {

   public enum AtsFolder {
      Ats_Heading("Action Tracking System", "ats.HeadingFolder", CoreArtifactTypes.Folder),
      Teams("Teams", "osee.ats.TopTeamDefinition", AtsArtifactTypes.TeamDefinition),
      ActionableItem("Actionable Items", "osee.ats.TopActionableItem", AtsArtifactTypes.ActionableItem),
      WorkFlow("Work Flows", "Work Flows", CoreArtifactTypes.Folder),
      WorkRules("Work Rules", "Work Rules", CoreArtifactTypes.Folder),
      WorkWidgets("Work Widgets", "Work Widgets", CoreArtifactTypes.Folder),
      WorkPages("Work Pages", "Work Pages", CoreArtifactTypes.Folder);
      protected final String displayName;
      protected final String staticId;
      protected final IArtifactType artifactType;

      private AtsFolder(String displayName, String staticId, IArtifactType artifactType) {
         this.displayName = displayName;
         this.staticId = staticId;
         this.artifactType = artifactType;
      }

      /**
       * @return the displayName
       */
      public String getDisplayName() {
         return displayName;
      }

      /**
       * @return the staticId
       */
      public String getStaticId() {
         return staticId;
      }
   }

   public static Map<AtsFolder, Artifact> folderMap = new HashMap<AtsFolder, Artifact>();
   public static String FOLDER_ARTIFACT = "Folder";

   public static Artifact getFolder(AtsFolder atsFolder) throws OseeCoreException {
      if (!folderMap.containsKey(atsFolder)) {
         Artifact artifact =
               StaticIdManager.getSingletonArtifact(atsFolder.artifactType, atsFolder.staticId, AtsUtil.getAtsBranch(),
                     true);
         if (artifact == null) {
            throw new OseeStateException(String.format("Can't retrieve Ats folder [%s]", atsFolder.displayName));
         }
         folderMap.put(atsFolder, artifact);
      }
      return folderMap.get(atsFolder);
   }

   public static void createAtsFolders() throws OseeCoreException {
      SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Create ATS Folders");

      Artifact headingArt =
            OseeSystemArtifacts.getOrCreateArtifact(CoreArtifactTypes.Folder, AtsFolder.Ats_Heading.displayName,
                  AtsUtil.getAtsBranch());
      if (!headingArt.hasParent()) {
         Artifact rootArt = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(AtsUtil.getAtsBranch());
         rootArt.addChild(headingArt);
         StaticIdManager.setSingletonAttributeValue(headingArt, AtsFolder.Ats_Heading.staticId);
         headingArt.persist(transaction);
      }

      for (AtsFolder atsFolder : AtsFolder.values()) {
         if (atsFolder == AtsFolder.Ats_Heading) {
            continue;
         }
         Artifact art =
               OseeSystemArtifacts.getOrCreateArtifact(atsFolder.artifactType, atsFolder.displayName,
                     AtsUtil.getAtsBranch());
         StaticIdManager.setSingletonAttributeValue(art, atsFolder.staticId);
         headingArt.addChild(art);
         art.persist(transaction);
      }

      transaction.execute();
   }

}
