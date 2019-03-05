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
package org.eclipse.osee.framework.core.enums;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.AccessControlModel;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Folder;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.GeneralData;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.RootArtifact;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.UniversalGroup;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.XViewerGlobalCustomization;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.data.UserGroupArtifactToken;

/**
 * @author Ryan D. Brooks
 */
public final class CoreArtifactTokens {

   // @formatter:off
   public static final IUserGroupArtifactToken Everyone             = UserGroupArtifactToken.valueOf(48656L, "Everyone");
   public static final IUserGroupArtifactToken OseeAccessAdmin      = UserGroupArtifactToken.valueOf(8033605L, "OseeAccessAdmin");
   public static final IUserGroupArtifactToken OseeAdmin            = UserGroupArtifactToken.valueOf(52247L, "OseeAdmin");
   public static final IUserGroupArtifactToken OseeDeveloper        = UserGroupArtifactToken.valueOf(464565465L, "OseeDeveloper");

   public static final ArtifactToken AccessIdMap          = ArtifactToken.valueOf(9885202, "Access Id Map - 0.26", COMMON, GeneralData);
   public static final ArtifactToken DataRightsFooters    = ArtifactToken.valueOf(5443258, "DataRightsFooters", COMMON, GeneralData);
   public static final ArtifactToken DefaultHierarchyRoot = ArtifactToken.valueOf(197818, "Default Hierarchy Root", RootArtifact);
   public static final ArtifactToken FrameworkAccessModel = ArtifactToken.valueOf(35975422, "Framework Access Model", COMMON, AccessControlModel);
   public static final ArtifactToken GlobalPreferences    = ArtifactToken.valueOf(18026, CoreArtifactTypes.GlobalPreferences.getName(), COMMON, CoreArtifactTypes.GlobalPreferences);
   public static final ArtifactToken UniversalGroupRoot   = ArtifactToken.valueOf(60807, "Root Artifact", UniversalGroup);
   public static final ArtifactToken XViewerCustomization = ArtifactToken.valueOf(78293, XViewerGlobalCustomization.getName(), COMMON, XViewerGlobalCustomization);

   // folders
   public static final ArtifactToken CustomerReqFolder    = ArtifactToken.valueOf(239420308, "Customer Requirements", Folder);
   public static final ArtifactToken DocumentTemplates    = ArtifactToken.valueOf(64970, "Document Templates", COMMON, Folder);
   public static final ArtifactToken FeaturesFolder       = ArtifactToken.valueOf(239420307, "Features", Folder);
   public static final ArtifactToken GitRepoFolder        = ArtifactToken.valueOf(111111111, "Git Repositories", Folder);
   public static final ArtifactToken OseeTypesFolder      = ArtifactToken.valueOf(7911256, "OSEE Types and Access Control", COMMON, Folder);
   public static final ArtifactToken ProductLineFolder    = ArtifactToken.valueOf(8255179, "Product Line", Folder);
   public static final ArtifactToken UserGroups           = ArtifactToken.valueOf(80920, "User Groups", COMMON, Folder);
   public static final ArtifactToken VariantsFolder       = ArtifactToken.valueOf(10039752, "Variants", Folder);
   // @formatter:on

   private CoreArtifactTokens() {
      // Constants
   }
}