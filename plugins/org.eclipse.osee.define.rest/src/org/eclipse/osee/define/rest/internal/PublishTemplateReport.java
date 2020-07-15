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

package org.eclipse.osee.define.rest.internal;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author David W. Miller
 */

public final class PublishTemplateReport implements StreamingOutput {
   private final QueryFactory queryApi;
   private final IOseeBranch branch;
   private final ArtifactId view;
   private final ArtifactId reportTemplateArt;
   private final ActivityLog activityLog;
   private ExcelXmlWriter writer;
   private final GenericReportBuilder report;

   public PublishTemplateReport(ActivityLog activityLog, OrcsApi orcsApi, BranchId branch, ArtifactId view, ArtifactId templateArt) {
      this.activityLog = activityLog;
      this.queryApi = orcsApi.getQueryFactory();
      this.branch = orcsApi.getQueryFactory().branchQuery().andId(branch).getResultsAsId().getExactlyOne();
      this.view = view;
      this.reportTemplateArt = templateArt;
      report = new GenericReportBuilder(branch, view);
   }

   @Override
   public void write(OutputStream output) {
      try {
         writer = new ExcelXmlWriter(new OutputStreamWriter(output, "UTF-8"));
         writeReport();
         writer.endWorkbook();
      } catch (Exception ex) {
         throw new WebApplicationException(ex);
      }
   }

   private void writeReport() throws IOException {
      sample_code_2();
      int numColumns = report.getColumnCount();
      writer.startSheet("Sheet 1", numColumns);
      doQuery();
      List<Object[]> data = new ArrayList<>();
      report.getDataRows(data);
      for (Object[] row : data) {
         writer.writeRow(row);
      }
      writer.endSheet();
   }

   private void doQuery() {
      QueryBuilder query = queryApi.fromBranch(branch, view);
      report.generateDataTree(query);
   }

   /*
    * level types - id with relation type collector
    */
   private void sample_code_1() {
    //@formatter:off
      report.level("Allocation Report").
        id(579055L).
        column("Artifact Id").
        column("Allocation Parent Name", CoreAttributeTypes.Name);
      report.level("System Requirements").
        relation(CoreRelationTypes.Allocation_Requirement).
        column("System Req Name", CoreAttributeTypes.Name);
      report.level("Software Requirements").
        relation(CoreRelationTypes.RequirementTrace_LowerLevelRequirement).
        column(CoreAttributeTypes.Name).
        column(CoreAttributeTypes.IDAL);
     //@formatter:on
   }

   private void sample_code_2() {
      //@formatter:off
        report.level("System Functions").
          type(CoreArtifactTypes.SystemFunctionMsWord).
          column("Artifact Id").
          column("System Function Name", CoreAttributeTypes.Name).
          column(CoreAttributeTypes.FDAL).
          column(CoreAttributeTypes.FdalRationale);

        report.level("Subsystem Functions").
          relation(CoreRelationTypes.Dependency_Dependency).
          column("Artifact Id").
          column("Subsystem Function Name", CoreAttributeTypes.Name).
          column(CoreAttributeTypes.FDAL).
          column(CoreAttributeTypes.FdalRationale);

        report.level("Subsystem Requirements").
          relation(CoreRelationTypes.Design_Requirement).
          column("Artifact Id").
          column("Subsystem Requirement Name", CoreAttributeTypes.Name).
          column(CoreAttributeTypes.IDAL).
          column(CoreAttributeTypes.IdalRationale);

        report.level("Software Requirements").
          relation(CoreRelationTypes.RequirementTrace_LowerLevelRequirement).
          column("Artifact Id").
          column("Software Requirement Name", CoreAttributeTypes.Name).
          column(CoreAttributeTypes.IDAL).
          column(CoreAttributeTypes.IdalRationale);
       //@formatter:on
   }
}