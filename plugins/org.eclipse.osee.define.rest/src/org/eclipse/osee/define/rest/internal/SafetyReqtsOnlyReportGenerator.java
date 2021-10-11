/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.define.rest.internal;

import java.io.IOException;
import java.io.Writer;
import org.eclipse.osee.define.rest.internal.util.ComponentUtil;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author David W. Miller
 */
public class SafetyReqtsOnlyReportGenerator {
   private final QueryFactory queryFactory;
   private final ComponentUtil componentUtil;
   private final ArtifactId view;
   private final BranchId branchId;

   private final String[] columnHeadings = {
      CoreArtifactTypes.SoftwareRequirementMsWord.getName(),
      "Development Assurance Level",
      "SW Partition",
      "SW CSU",
      "Requirement Type"};

   public SafetyReqtsOnlyReportGenerator(OrcsApi orcsApi, BranchId branchId, ArtifactId view) {
      queryFactory = orcsApi.getQueryFactory();
      this.view = view;
      this.branchId = branchId;
      componentUtil = new ComponentUtil(branchId, orcsApi);
   }

   private void writeCell(String value, String[] currentRow, int col) {
      currentRow[col] = value;
   }

   public void runOperation(Writer providedWriter) throws IOException {
      ISheetWriter writer = new ExcelXmlWriter(providedWriter);
      generateReport(writer);
      writer.endWorkbook();
   }

   private void generateReport(ISheetWriter writer) throws IOException {
      writer.startSheet("Software Requirement Report", columnHeadings.length);
      writer.writeRow((Object[]) columnHeadings);
      String[] currentRowValues = new String[columnHeadings.length];

      ResultSet<ArtifactReadable> softwareReqts = queryFactory.fromBranch(branchId, view).andIsOfType(
         CoreArtifactTypes.AbstractSoftwareRequirement).getResults();
      for (ArtifactReadable softwareRequirement : softwareReqts) {
         processSoftwareRequirement(softwareRequirement, currentRowValues);
         writer.writeRow((Object[]) currentRowValues);
      }
      writer.endSheet();
   }

   private void writeCriticality(ArtifactReadable art, AttributeTypeToken thisType, String[] currentRowValues, int col) {
      String current = art.getSoleAttributeAsString(thisType, "Error");
      if ("Error".equals(current)) {
         writeCell("Error: invalid content", currentRowValues, col);
      } else if (AttributeId.UNSPECIFIED.equals(current)) {
         writeCell(AttributeId.UNSPECIFIED, currentRowValues, col);
      } else {
         writeCell(current, currentRowValues, col);
      }
   }

   private void processSoftwareRequirement(ArtifactReadable softwareRequirement, String[] currentRowValues) {
      writeCell(softwareRequirement.getName(), currentRowValues, 0);
      writeCriticality(softwareRequirement, CoreAttributeTypes.IDAL, currentRowValues, 1);
      writeCell(softwareRequirement.getAttributeValuesAsString(CoreAttributeTypes.Partition), currentRowValues, 2);
      try {
         writeCell(componentUtil.getQualifiedComponentNames(softwareRequirement), currentRowValues, 3);
      } catch (Exception ex) {
         writeCell("No component", currentRowValues, 3);
      }
      writeCell(softwareRequirement.getArtifactType().getName(), currentRowValues, 4);
   }

   public ComponentUtil getComponentUtil() {
      return componentUtil;
   }
}
