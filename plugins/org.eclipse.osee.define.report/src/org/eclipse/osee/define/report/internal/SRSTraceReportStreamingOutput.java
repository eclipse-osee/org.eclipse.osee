/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.report.internal;

import static org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelColumn.newCol;
import static org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter.WrappedStyle;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.CaseInsensitiveString;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.BranchReadable;
import org.eclipse.osee.orcs.search.BranchQuery;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author Ryan D. Brooks
 * @author David W. Miller
 */
public final class SRSTraceReportStreamingOutput implements StreamingOutput {
   private final QueryFactory queryFactory;
   private final BranchReadable branch;
   private final String codeRoot;
   private final TraceAccumulator traceAccumulator;
   private final Map<CaseInsensitiveString, ArtifactReadable> nameToReqMap =
      new LinkedHashMap<CaseInsensitiveString, ArtifactReadable>();
   private final String csci;
   private final String traceType;
   private final List<ArtifactReadable> noTraceReqs = new ArrayList<ArtifactReadable>();
   private ExcelXmlWriter writer;
   private static final IArtifactType WCAFE = TokenFactory.createArtifactType(0x0000BA000000001FL, "WCAFE");
   private final Log logger;

   public SRSTraceReportStreamingOutput(Log logger, OrcsApi orcsApi, long branchUuid, String codeRoot, TraceAccumulator traceAccumulator, String csci, String traceType) {
      this.queryFactory = orcsApi.getQueryFactory(null);
      BranchQuery branchQuery = orcsApi.getQueryFactory(null).branchQuery();
      this.branch = branchQuery.andUuids(branchUuid).getResults().getExactlyOne();
      this.codeRoot = codeRoot.trim();
      this.traceAccumulator = traceAccumulator;
      this.csci = csci;
      this.traceType = traceType;
      this.logger = logger;
   }

   @Override
   public void write(OutputStream output) {
      try {
         Date start = new Date();
         logger.debug("Started writing SRS Trace Report", start);
         traceAccumulator.extractTraces(new File(codeRoot));
         writer = new ExcelXmlWriter(new OutputStreamWriter(output, "UTF-8"));

         writeReqSheet();
         writeCodeSheet();
         List<Object[]> missingSrsRows = writeSrsToCodeSheet();
         writeSrsMissing(missingSrsRows);
         writeNoTraceFilesSheet();
         writeNoTraceReqsSheet();
         writeMalformedTraceMarks();
         writeSwReqToSubsystemReq();
         writeSummary(start);

         writer.endWorkbook();
      } catch (Exception ex) {
         throw new WebApplicationException(ex);
      }
   }

   private void writeReqSheet() throws IOException {
      writer.startSheet("Req to Code", newCol("Paragraph #", 85), newCol("Req Name", 400),
         newCol("Requirement Type", 120), newCol("Code Unit", 500, WrappedStyle));

      QueryBuilder query =
         queryFactory.fromBranch(branch).andIsOfType(CoreArtifactTypes.AbstractSoftwareRequirement).and(
            CoreAttributeTypes.Csci, csci);

      for (ArtifactReadable req : query.getResults().sort(new ParagraphNumberComparator(logger))) {
         String reqName = req.getName();

         if (req.isOfType(WCAFE) || isNoTrace(reqName)) {
            continue;
         }
         nameToReqMap.put(new CaseInsensitiveString(reqName), req);
         ArrayList<String> codeUnits = new ArrayList<String>(traceAccumulator.getFiles(req.getName()));

         if (codeUnits.isEmpty()) {
            noTraceReqs.add(req);
         }

         java.util.Collections.sort(codeUnits);
         writeReq(req, reqName);
         writer.writeCell(Collections.toString("\n", codeUnits));
         writer.endRow();
      }

      writer.endSheet();
   }

   private boolean isNoTrace(String reqName) {
      return reqName.equals("CSID None") || reqName.equals("SubDD None");
   }

   private void writeReq(ArtifactReadable req, String reqName) throws IOException {
      writer.writeCell(req.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, ""));
      writer.writeCell(reqName);
      writer.writeCell(req.getArtifactType());
   }

   private void writeCodeSheet() throws IOException {
      writer.startSheet("Code to Req", newCol("Code Unit", 500), newCol("Paragraph #", 85, WrappedStyle),
         newCol("Req Name", 400, WrappedStyle), newCol("Requirement Type", 120, WrappedStyle),
         newCol("No Match", 400, WrappedStyle));

      StringBuilder paragraphNums = new StringBuilder(1000);
      StringBuilder names = new StringBuilder(2000);
      StringBuilder types = new StringBuilder(1500);
      StringBuilder noMatches = new StringBuilder(1500);

      for (String codeUnit : traceAccumulator.getFiles()) {
         buildReqInfo(traceAccumulator.getTraceMarks(codeUnit), paragraphNums, names, types, noMatches);

         writer.writeRow(codeUnit, paragraphNums, names, types, noMatches);

         // efficiently reset the StringBuilders to be empty (but preserve their capacities)
         paragraphNums.setLength(0);
         names.setLength(0);
         types.setLength(0);
         noMatches.setLength(0);
      }

      writer.endSheet();
   }

   private void buildReqInfo(Set<CaseInsensitiveString> reqNames, StringBuilder paragraphNums, StringBuilder names, StringBuilder types, StringBuilder noMatches) {
      boolean firstReq = true;
      boolean firstNoMatch = true;

      ArrayList<CaseInsensitiveString> sortedReqNames = new ArrayList<CaseInsensitiveString>(reqNames);
      java.util.Collections.sort(sortedReqNames);

      for (CaseInsensitiveString ciReqName : sortedReqNames) {
         String reqName = ciReqName.toString();
         if (!reqName.startsWith("SRS")) {

            ArtifactReadable req = nameToReqMap.get(ciReqName);
            if (req == null) {
               if (firstNoMatch) {
                  firstNoMatch = false;
               } else {
                  noMatches.append("\n");
               }

               noMatches.append(reqName);
            } else {
               if (firstReq) {
                  firstReq = false;
               } else {
                  paragraphNums.append("\n");
                  names.append("\n");
                  types.append("\n");
               }

               paragraphNums.append(req.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, ""));
               names.append(req.getName());
               types.append(req.getArtifactType());
            }
         }
      }
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

   private List<Object[]> writeSrsToCodeSheet() throws IOException {
      writer.startSheet("SRS to Code", newCol("SRS Name", 400), newCol("Req Name", 400),
         newCol("Requirement Type", 120), newCol("Code Unit", 500, WrappedStyle));

      ArtifactReadable srsHeadingParent =
         queryFactory.fromBranch(branch).andNameEquals("SRS Headings").getResults().getExactlyOne();

      ResultSet<ArtifactReadable> srsHeadings = null;
      for (ArtifactReadable folder : srsHeadingParent.getChildren()) {
         if (folder.getName().startsWith(csci)) {
            srsHeadings = folder.getChildren();
         }
      }

      Set<String> sharedCodeUnits = new HashSet<String>();
      List<Object[]> missingSrsRows = new ArrayList<Object[]>();

      for (ArtifactReadable srs : srsHeadings) {
         String srsName = srs.getName();
         Set<String> srsCodeUnits = traceAccumulator.getFiles("SRS" + srsName);

         for (ArtifactReadable req : srs.getRelated(CoreRelationTypes.SupportingInfo_SupportedBy)) {
            String reqName = req.getName();
            if (isNoTrace(reqName)) {
               continue;
            }
            Set<String> reqCodeUnits = traceAccumulator.getFiles(reqName);

            sharedCodeUnits.addAll(srsCodeUnits);
            sharedCodeUnits.retainAll(reqCodeUnits);
            writer.writeRow(srsName, reqName, req.getArtifactType(), Collections.toString("\n", sharedCodeUnits));

            if (sharedCodeUnits.isEmpty()) {
               missingSrsRows.add(new String[] {srsName, reqName});
            }
            sharedCodeUnits.clear();
         }
      }
      writer.endSheet();

      return missingSrsRows;
   }

   private void writeSrsMissing(List<Object[]> missingSrsRows) throws IOException {
      writer.startSheet("No Trace SRS", newCol("SRS Name", 400), newCol("Req Name", 400));

      for (Object[] row : missingSrsRows) {
         writer.writeRow(row);
      }

      missingSrsRows.clear();
      writer.endSheet();
   }

   private void writeMalformedTraceMarks() throws IOException {
      writer.startSheet("Malformed Traces", newCol("File", 400), newCol("Malformed Trace Marks", 400));
      for (String file : traceAccumulator.getFilesWithMalformedMarks()) {
         Set<String> malformedMarks = traceAccumulator.getMalformedMarks(file);
         writer.writeRow(file, Collections.toString("\n", malformedMarks));
      }
      writer.endSheet();
   }

   private void writeSwReqToSubsystemReq() throws IOException {
      writer.startSheet("Req To PIDs", newCol("Paragraph #", 85), newCol("Req Name", 400),
         newCol("Requirement Type", 120), newCol("Subsystem Requirement", 400, WrappedStyle));

      StringBuilder higherLevelStrB = new StringBuilder(1000);
      for (ArtifactReadable req : nameToReqMap.values()) {
         writeReq(req, req.getName());

         boolean firstReq = true;
         for (ArtifactReadable highLevelReq : req.getRelated(CoreRelationTypes.Requirement_Trace__Higher_Level)) {
            if (firstReq) {
               firstReq = false;
            } else {
               higherLevelStrB.append("\n");
            }

            higherLevelStrB.append(highLevelReq.getName());
         }

         writer.writeCell(higherLevelStrB);
         writer.endRow();
         // efficiently reset the StringBuilders to be empty (but preserve their capacities)
         higherLevelStrB.setLength(0);
      }

      writer.endSheet();
   }

   private void writeSummary(Date start) throws IOException {
      writer.startSheet("Summary", newCol("Description", 400), newCol("Value", 400));

      writer.writeRow("Code Root", codeRoot);
      writer.writeRow("Branch Name", branch.getName());
      Date now = Calendar.getInstance().getTime();

      writer.writeRow("Report Date", now.toString());
      writer.writeRow("Elapsed Time", Lib.getElapseString(start.getTime()));
      writer.writeRow("CSCI", csci);
      writer.writeRow("Trace Type", traceType);
      writer.endSheet();
      logger.debug("Finished writing SRS Trace Report", now);
   }
}