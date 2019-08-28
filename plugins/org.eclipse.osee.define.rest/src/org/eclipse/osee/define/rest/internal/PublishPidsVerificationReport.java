/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.rest.internal;

import static org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelColumn.newCol;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author David Miller
 * @author Branden Phillips
 */

public final class PublishPidsVerificationReport implements StreamingOutput {
   private final OrcsApi orcsApi;
   private final BranchId branch;
   private final ActivityLog activityLog;
   private final ArtifactId root;
   private ExcelXmlWriter writer;

   public PublishPidsVerificationReport(ActivityLog activityLog, OrcsApi orcsApi, BranchId branch, ArtifactId root) {
      this.activityLog = activityLog;
      this.branch = branch;
      this.orcsApi = orcsApi;
      this.root = root;
   }

   @Override
   public void write(OutputStream output) {
      try {

         writer = new ExcelXmlWriter(new OutputStreamWriter(output, "UTF-8"));

         writePidsVerificationSheet();
         writer.endWorkbook();
      } catch (Exception ex) {
         throw new WebApplicationException(ex);
      }
   }

   private void writePidsVerificationSheet() throws IOException {

      writer.startSheet("PIDS Traceability to Results", newCol(null, 320), newCol(null, 200), newCol(null, 320),
         newCol(null, 320));

      QueryBuilder query =
         orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.SubsystemRequirementMsWord);

      String[] row = {"Subsystem Requirement", "Subsystem", "Verification Trace", "Results Trace"};
      writer.writeRow((Object[]) row);

      for (ArtifactReadable req : query.getResults()) {
         String sub = req.getSoleAttributeAsString(CoreAttributeTypes.Subsystem, "Not Set");
         getVerificationsForRequirement(sub, req);
      }

      writer.endSheet();
   }

   private void getVerificationsForRequirement(String sub, ArtifactReadable req) throws IOException {
      boolean noVerifications = true;

      for (ArtifactReadable ver : req.getRelated(CoreRelationTypes.Verification__Verifier).getList()) {
         noVerifications = false;
         getResultsForVerifications(sub, req, ver);
      }
      if (noVerifications) {
         String[] row = {req.getName(), sub, "NO VERIFICATION TRACE", "NO RESULTS TRACE"};
         writer.writeRow((Object[]) row);
      }
   }

   private void getResultsForVerifications(String sub, ArtifactReadable req, ArtifactReadable ver) throws IOException {
      boolean noResults = true;

      for (ArtifactReadable res : ver.getRelated(CoreRelationTypes.Test_Unit_Result__Test_Result).getList()) {
         noResults = false;
         String[] row = {req.getName(), sub, ver.getName(), res.getName()};
         writer.writeRow((Object[]) row);
      }
      if (noResults) {
         String[] row = {req.getName(), sub, ver.getName(), "NO RESULTS"};
         writer.writeRow((Object[]) row);
      }
   }
}