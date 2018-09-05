/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.utility;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.BranchView;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.FeatureDefinition;
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
}