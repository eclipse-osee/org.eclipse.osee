/*********************************************************************
 * Copyright (c) 2011 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.core.enums;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Folder;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.GeneralData;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.RootArtifact;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.UniversalGroup;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.XViewerGlobalCustomization;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * @author Ryan D. Brooks
 */
public final class CoreArtifactTokens {

   // @formatter:off

   public static final ArtifactToken AccessIdMap          = ArtifactToken.valueOf(9885202, "Access Id Map - 0.26", COMMON, GeneralData);
   public static final ArtifactToken DataRightsFooters    = ArtifactToken.valueOf(5443258, "DataRightsFooters", COMMON, GeneralData);
   public static final ArtifactToken DefaultHierarchyRoot = ArtifactToken.valueOf(197818, "Default Hierarchy Root", RootArtifact);
   public static final ArtifactToken GlobalPreferences    = ArtifactToken.valueOf(18026, CoreArtifactTypes.GlobalPreferences.getName(), COMMON, CoreArtifactTypes.GlobalPreferences);
   public static final ArtifactToken UniversalGroupRoot   = ArtifactToken.valueOf(60807, "Root Artifact", UniversalGroup);
   public static final ArtifactToken XViewerCustomization = ArtifactToken.valueOf(78293, XViewerGlobalCustomization.getName(), COMMON, XViewerGlobalCustomization);

   // folders
   public static final ArtifactToken OseeConfiguration    = ArtifactToken.valueOf(10525153, "OSEE Configuration", Folder);
   public static final ArtifactToken CustomerReqFolder    = ArtifactToken.valueOf(239420308, "Customer Requirements", Folder);
   public static final ArtifactToken DocumentTemplates    = ArtifactToken.valueOf(64970, "Document Templates", COMMON, Folder);
   public static final ArtifactToken FeaturesFolder       = ArtifactToken.valueOf(239420307, "Features", Folder);
   public static final ArtifactToken GitRepoFolder        = ArtifactToken.valueOf(111111111, "Git Repositories", Folder);
   public static final ArtifactToken ProductLineFolder    = ArtifactToken.valueOf(8255179, "Product Line", Folder);
   public static final ArtifactToken UserGroups           = ArtifactToken.valueOf(80920, "User Groups", COMMON, Folder);
   public static final ArtifactToken ProductsFolder       = ArtifactToken.valueOf(10039752, "Products", Folder);
   public static final ArtifactToken PlCfgGroupsFolder    = ArtifactToken.valueOf(10862035, "PL Configuration Groups", Folder);

   public static final ArtifactToken HardwareRequirementsFolder    = ArtifactToken.valueOf(90872, "Hardware Requirements", Folder);
   public static final ArtifactToken SystemRequirementsFolder    = ArtifactToken.valueOf(121523, "System Requirements", Folder);
   public static final ArtifactToken SubSystemRequirementsFolder    = ArtifactToken.valueOf(119106, "SubSystem Requirements", Folder);
   public static final ArtifactToken SoftwareRequirementsFolder    = ArtifactToken.valueOf(37334, "Software Requirements", Folder);
   public static final ArtifactToken TestSupportUnitsFolder    = ArtifactToken.valueOf(34522345, "Test Support Units", Folder);

   public static final ArtifactToken InterfaceMessagesFolder = ArtifactToken.valueOf(2121138078, "Interface Messages", Folder);
   public static final ArtifactToken InterfacePlatformTypesFolder = ArtifactToken.valueOf(2121138079, "Interface Platform Types", Folder);
   // @formatter:on

   public static final String SAW_PRODUCT_DECOMP = "SAW Product Decomposition";

   private CoreArtifactTokens() {
      // Constants
   }

}