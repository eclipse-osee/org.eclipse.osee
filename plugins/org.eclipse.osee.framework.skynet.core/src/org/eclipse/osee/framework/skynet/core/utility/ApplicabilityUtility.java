/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.framework.skynet.core.utility;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.BranchView;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.eclipse.osee.framework.core.applicability.FeatureDefinition;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.orcs.rest.client.OseeClient;

/**
 * @author David W. Miller
 */
public class ApplicabilityUtility {

   public static HashCollection<String, String> getValidFeatureValuesForBranch(BranchId branch) {

      OseeClient oseeClient = ServiceUtil.getOseeClient();

      List<FeatureDefinition> featureDefinitionData =
         oseeClient.getApplicabilityEndpoint(branch).getFeatureDefinitionData();

      HashCollection<String, String> validFeatureValues = new HashCollection<>();
      for (FeatureDefinition feat : featureDefinitionData) {
         validFeatureValues.put(feat.getName().toUpperCase(), feat.getValues());
      }

      return validFeatureValues;
   }

   public static HashSet<String> getBranchViewNamesUpperCase(BranchId branch) {
      HashSet<String> names = new HashSet<>();

      Collection<ArtifactToken> views = ArtifactQuery.getArtifactTokenListFromType(BranchView, branch);
      for (ArtifactToken view : views) {
         names.add(view.getName().toUpperCase());
      }
      return names;
   }

   public static HashSet<String> getConfigurationGroupsUpperCase(BranchId branch) {
      HashSet<String> names = new HashSet<>();
      OseeClient oseeClient = ServiceUtil.getOseeClient();
      oseeClient.getApplicabilityEndpoint(branch).getViews();
      Collection<ArtifactToken> views =
         ArtifactQuery.getArtifactTokenListFromType(CoreArtifactTypes.GroupArtifact, branch);
      for (ArtifactToken view : views) {
         names.add(view.getName().toUpperCase());
      }
      return names;
   }

}