/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.orcs.rest.internal.writer;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.applicability.FeatureDefinition;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Audrey E. Denk
 */
public class ApplicabilityFeatureMatrixGenerator {

   private final OrcsApi orcsApi;

   public ApplicabilityFeatureMatrixGenerator(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public void runOperation(OrcsApi providedOrcs, Writer providedWriter, BranchId branch, String filter) throws IOException {
      ISheetWriter writer = new ExcelXmlWriter(providedWriter);
      createConfigSheet(writer, branch, filter);
      createGroupsSheet(writer, branch, filter);
      writer.endWorkbook();
   }

   private void createConfigSheet(ISheetWriter writer, BranchId branch, String filter) throws IOException {
      List<String> headingsList = new ArrayList<String>();
      headingsList.add("Feature");
      headingsList.add("Description");
      List<ArtifactToken> configurationsForBranch =
         orcsApi.getQueryFactory().applicabilityQuery().getConfigurationsForBranch(branch);
      if (Strings.isValid(filter)) {
         configurationsForBranch.removeIf(art -> art.getName().matches(filter));
      }
      for (ArtifactToken art : configurationsForBranch) {
         headingsList.add(art.getName());
      }
      Object[] headings = headingsList.toArray();
      writer.startSheet("Configurations", configurationsForBranch.size() + 2);
      writer.writeRow(headings);
      printMatrix(writer, branch, configurationsForBranch);

      writer.endSheet();
   }

   private void createGroupsSheet(ISheetWriter writer, BranchId branch, String filter) throws IOException {
      List<String> headingsList = new ArrayList<String>();
      headingsList.add("Feature");
      headingsList.add("Description");
      List<ArtifactToken> configurationsForBranch =
         orcsApi.getQueryFactory().applicabilityQuery().getConfigurationGroupsForBranch(branch);
      if (Strings.isValid(filter)) {
         configurationsForBranch.removeIf(art -> art.getName().matches(filter));
      }
      for (ArtifactToken art : configurationsForBranch) {
         headingsList.add(art.getName().toString());
      }
      Object[] headings = headingsList.toArray();
      writer.startSheet("Configuration Groups", configurationsForBranch.size() + 2);
      writer.writeRow(headings);
      printMatrix(writer, branch, configurationsForBranch);
      writer.endSheet();
   }

   private void printMatrix(ISheetWriter writer, BranchId branch, List<ArtifactToken> configurationsForBranch) throws IOException {
      List<FeatureDefinition> featureDefinitionData =
         orcsApi.getQueryFactory().applicabilityQuery().getFeatureDefinitionData(branch);

      Collections.sort(featureDefinitionData, new Comparator<FeatureDefinition>() {
         @Override
         public int compare(FeatureDefinition obj1, FeatureDefinition obj2) {
            return obj1.getName().compareTo(obj2.getName());
         }
      });

      Map<ArtifactId, Map<String, List<String>>> branchViewsMap = new HashMap<>();

      for (ArtifactId view : configurationsForBranch) {
         branchViewsMap.put(view,
            orcsApi.getQueryFactory().applicabilityQuery().getNamedViewApplicabilityMap(branch, view));
      }
      for (FeatureDefinition featureDefinition : featureDefinitionData) {
         writer.writeCell(featureDefinition.getName());
         writer.writeCell(featureDefinition.getDescription());
         for (ArtifactId view : configurationsForBranch) {
            List<String> list = branchViewsMap.get(view).get(featureDefinition.getName());
            // every view should have a value for each feature, if incorrectly configured returns null
            if (list != null) {
               writer.writeCell(org.eclipse.osee.framework.jdk.core.util.Collections.toString(",", list));
            } else {
               writer.writeEmptyCell();
            }
         }
         writer.endRow();
      }
   }
}
