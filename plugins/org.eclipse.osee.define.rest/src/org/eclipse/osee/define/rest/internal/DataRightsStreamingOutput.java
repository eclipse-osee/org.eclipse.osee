/*******************************************************************************
 * Copyright (c) 2015 Boeing.
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
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.define.api.ParagraphNumberComparator;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.CaseInsensitiveString;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.BranchQuery;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author Ryan D. Brooks
 */
public final class DataRightsStreamingOutput implements StreamingOutput {
   private final QueryFactory queryFactory;
   private final IOseeBranch branch;
   private final String codeRoot;
   private final TraceAccumulator traceAccumulator;
   private final Map<CaseInsensitiveString, ArtifactReadable> nameToReqMap = new LinkedHashMap<>();
   private final List<ArtifactReadable> noTraceReqs = new ArrayList<>();
   private ExcelXmlWriter writer;
   private final ActivityLog activityLog;
   private static final ArtifactTypeId WCAFE = ArtifactTypeId.valueOf(204509162766367L);

   public DataRightsStreamingOutput(OrcsApi orcsApi, BranchId branch, String codeRoot, TraceAccumulator traceAccumulator, ActivityLog activityLog) {

      this.queryFactory = orcsApi.getQueryFactory();
      BranchQuery branchQuery = orcsApi.getQueryFactory().branchQuery();
      this.branch = branchQuery.andId(branch).getResults().getExactlyOne();
      this.codeRoot = codeRoot.trim();
      this.traceAccumulator = traceAccumulator;
      this.activityLog = activityLog;
   }

   @Override
   public void write(OutputStream output) {
      try {
         Date startTime = new Date();
         traceAccumulator.extractTraces(new File(codeRoot));
         writer = new ExcelXmlWriter(new OutputStreamWriter(output, "UTF-8"));

         writeReqCodeRightsSheet();
         writeNoTraceFilesSheet();
         writeNoTraceReqsSheet();
         writeInvalidTraceSheet();
         writeSummary(startTime);

         writer.endWorkbook();
      } catch (Exception ex) {
         throw new WebApplicationException(ex);
      }
   }

   private void writeInvalidTraceSheet() throws IOException {
      writer.startSheet("Invalid Marks", newCol("File", 400), newCol("Mark Text", 400));
      for (String file : traceAccumulator.getFilesWithMalformedMarks()) {
         for (String mark : traceAccumulator.getMalformedMarks(file)) {
            writer.writeRow(file, mark);
         }
      }
      writer.endSheet();
   }

   private void writeReqCodeRightsSheet() throws IOException {
      writer.startSheet("Req & Code Rights", newCol("Req Name", 400), newCol("Requirement Type", 120),
         newCol("Req Data Rights", 100), newCol("Code Unit", 500), newCol("Code Data Rights", 100),
         newCol("Consistency Check", 100));

      QueryBuilder query = queryFactory.fromBranch(branch).andIsOfType(CoreArtifactTypes.AbstractSoftwareRequirement);

      for (ArtifactReadable req : query.getResults().sort(new ParagraphNumberComparator(activityLog))) {
         if (req.isOfType(WCAFE)) {
            continue;
         }

         String reqName = req.getName();

         nameToReqMap.put(new CaseInsensitiveString(reqName), req);
         ArrayList<String> codeUnits = new ArrayList<>(traceAccumulator.getFiles(reqName));

         if (codeUnits.isEmpty()) {
            writeReq(req, reqName);
            writer.endRow();
         } else {
            Collections.sort(codeUnits);
            for (String codeunit : codeUnits) {
               writeReq(req, reqName);
               writeCode(codeunit);
            }
         }
      }

      writer.endSheet();
   }

   private void writeReq(ArtifactReadable req, String reqName) throws IOException {
      writer.writeCell(reqName);
      writer.writeCell(req.getArtifactType());
      writer.writeCell(req.getSoleAttributeValue(CoreAttributeTypes.DataRightsClassification, "missing"));
   }

   private void writeCode(String path) throws IOException {
      writer.writeCell(path);
      writer.endRow();
   }

   private void writeNoTraceFilesSheet() throws IOException {
      writer.startSheet("No Trace Files", newCol(700));

      for (String path : traceAccumulator.getNoTraceFiles()) {
         writer.writeRow(path);
      }
      writer.endSheet();
   }

   private void writeNoTraceReqsSheet() throws IOException {
      writer.startSheet("No Trace Reqs", newCol("Paragraph #", 85), newCol("Req Name", 400),
         newCol("Requirement Type", 110));

      for (ArtifactReadable req : noTraceReqs) {
         writeReq(req, req.getName());
         writer.endRow();
      }
      writer.endSheet();
      noTraceReqs.clear();
   }

   private void writeSummary(Date start) throws IOException {
      writer.startSheet("Summary", newCol("Description", 400), newCol("Value", 400));

      writer.writeRow("Code Root", codeRoot);
      writer.writeRow("Branch Name", branch.getName());
      Date now = Calendar.getInstance().getTime();

      writer.writeRow("Report Date", now.toString());
      writer.writeRow("Elapsed Time", Lib.getElapseString(start.getTime()));
      writer.endSheet();
   }
}