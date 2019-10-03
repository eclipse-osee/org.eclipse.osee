/*******************************************************************************
 * Copyright (c) 2014 Boeing.
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
import static org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter.WrappedStyle;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.define.api.ParagraphNumberComparator;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.ArtifactTypes;
import org.eclipse.osee.orcs.search.BranchQuery;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author Marc Potter
 */

public final class PublishLowHighReqStreamingOutput implements StreamingOutput {
   private final QueryFactory queryApi;
   private final OrcsTypes types;
   private final IOseeBranch branch;
   private final ActivityLog activityLog;
   private final Map<String, Integer> summarySubsystemCounter = new HashMap<>();
   private final Map<String, Integer> summaryTraceCounter = new HashMap<>();
   private final Map<String, Integer> summaryAllocationCounter = new HashMap<>();
   private ExcelXmlWriter writer;
   private final Collection<ArtifactTypeToken> includeOnlyArtifactTypes;
   private final String REQUIREMENT_TRACE_TYPE = "Requirement Trace";
   private final String ALLOCATION_TRACE_TYPE = "Allocation Trace";
   private final Map<String, ArtifactTypeToken> allTypesMap = new HashMap<>();

   public PublishLowHighReqStreamingOutput(ActivityLog activityLog, OrcsApi orcsApi, BranchId branch, String selectedTypes) {
      this.activityLog = activityLog;
      this.queryApi = orcsApi.getQueryFactory();
      BranchQuery query = orcsApi.getQueryFactory().branchQuery();
      this.branch = query.andId(branch).getResultsAsId().getExactlyOne();
      this.types = orcsApi.getOrcsTypes();
      includeOnlyArtifactTypes = convertStringTypes(selectedTypes);
   }

   @Override
   public void write(OutputStream output) {
      try {

         writer = new ExcelXmlWriter(new OutputStreamWriter(output, "UTF-8"));

         writeLowHighReqSheet();
         // writeHighLowTraceSheet MUST be called before writeSummarySheet
         writeHighLowTraceSheet();
         writeSummarySheet();
         writer.endWorkbook();
      } catch (Exception ex) {
         throw new WebApplicationException(ex);
      }
   }

   private void writeLowHighReqSheet() throws IOException {

      writer.startSheet("Low to High Level Req", newCol(null, 85), newCol(null, 400), newCol(null, 120),
         newCol(null, 120, WrappedStyle), newCol(null, 85, WrappedStyle), newCol(null, 400, WrappedStyle),
         newCol(null, 350, WrappedStyle), newCol(null, 250, WrappedStyle));

      QueryBuilder query = queryApi.fromBranch(branch).andIsOfType(CoreArtifactTypes.AbstractSoftwareRequirement);
      String[] row = {"Low Level Requirement", null, null, null, "High Level Requirement", null, null, null};
      writer.writeRow((Object[]) row);
      row[0] = "Paragraph #";
      row[1] = "Req Name";
      row[2] = "Requirement Type";
      row[3] = "Qualification Method";
      row[4] = "Paragraph #";
      row[5] = "Req Name";
      row[6] = "Requirement Type";
      row[7] = "Subsystem";
      writer.writeRow((Object[]) row);

      if (includeOnlyArtifactTypes == null) {
         // nothing selected
         writer.endSheet();
         return;
      }
      StringBuilder[] builtRows = new StringBuilder[8];
      for (int i = 0; i < 8; i++) {
         builtRows[i] = new StringBuilder();
      }

      for (ArtifactReadable req : query.getResults().sort(new ParagraphNumberComparator(activityLog))) {
         boolean foundType = false;
         for (ArtifactTypeToken type : includeOnlyArtifactTypes) {
            if (req.isTypeEqual(type)) {
               foundType = true;
               break;
            }
         }
         if (!foundType) {
            continue;
         }
         for (int i = 0; i < 8; i++) {
            builtRows[i].setLength(0);
         }
         if (req.getAttributeCount(CoreAttributeTypes.ParagraphNumber) > 0) {
            builtRows[0].append(req.getSoleAttributeAsString(CoreAttributeTypes.ParagraphNumber));
         }
         builtRows[1].append(req.getName());
         builtRows[2].append(req.getArtifactType().getName());
         List<String> qualificationMethods = req.getAttributeValues(CoreAttributeTypes.QualificationMethod);
         if (qualificationMethods.size() > 0) {
            Iterator<String> iter = qualificationMethods.iterator();
            builtRows[3].append(iter.next());
            while (iter.hasNext()) {
               builtRows[3].append(",");
               builtRows[3].append(iter.next());
            }
         } else {
            row[3] = "";
         }
         String newline = "";
         for (ArtifactReadable subSysReq : req.getRelated(CoreRelationTypes.RequirementTrace_HigherLevelRequirement)) {
            builtRows[4].append(newline);
            builtRows[4].append(subSysReq.getSoleAttributeAsString(CoreAttributeTypes.ParagraphNumber));
            builtRows[5].append(newline);
            builtRows[5].append(subSysReq.getName());
            builtRows[6].append(newline);
            builtRows[6].append(subSysReq.getArtifactType().getName());
            String subsystem = "Not specified";
            if (req.getAttributeCount(CoreAttributeTypes.Subsystem) > 0) {
               subsystem = subSysReq.getSoleAttributeAsString(CoreAttributeTypes.Subsystem);
            }
            builtRows[7].append(newline);
            builtRows[7].append(subsystem);
            newline = "\n";
         }
         for (int i = 0; i < 8; i++) {
            row[i] = builtRows[i].toString();
         }
         nullEmptyCells(row);
         writer.writeRow((Object[]) row);
      }

      writer.endSheet();
   }

   private void writeHighLowTraceSheet() throws IOException {

      writer.startSheet("High to Low Level Req", newCol(null, 85), newCol(null, 400), newCol(null, 120),
         newCol(null, 120, WrappedStyle), newCol(null, 120, WrappedStyle), newCol(null, 85, WrappedStyle),
         newCol(null, 400, WrappedStyle), newCol(null, 350, WrappedStyle), newCol(null, 250, WrappedStyle));

      QueryBuilder query = queryApi.fromBranch(branch).andIsOfType(CoreArtifactTypes.AbstractSystemRequirement);
      String[] row = {"Higher Level Requirement", null, null, null, null, "Low Level Requirement", null, null, null};
      writer.writeRow((Object[]) row);
      row[0] = "Paragraph #";
      row[1] = "Req Name";
      row[2] = "Requirement Type";
      row[3] = "Subsystem";
      row[4] = "Relation Type";
      row[5] = "Paragraph #";
      row[6] = "Req Name";
      row[7] = "Requirement Type";
      row[8] = "Qualification Method";
      writer.writeRow((Object[]) row);

      StringBuilder[] builtRows = new StringBuilder[9];
      for (int i = 0; i < 9; i++) {
         builtRows[i] = new StringBuilder();
      }
      for (ArtifactReadable req : query.getResults().sort(new ParagraphNumberComparator(activityLog))) {
         row[0] = row[1] = row[2] = row[3] = row[4] = row[5] = row[6] = row[7] = row[8] = "";
         for (int i = 0; i < 9; i++) {
            builtRows[i].setLength(0);
         }
         if (req.getAttributeCount(CoreAttributeTypes.ParagraphNumber) > 0) {
            builtRows[0].append(req.getSoleAttributeAsString(CoreAttributeTypes.ParagraphNumber));
         }
         builtRows[1].append(req.getName());
         builtRows[2].append(req.getArtifactType().getName());
         String subsystem = "Not specified";
         if (req.getAttributeCount(CoreAttributeTypes.Subsystem) > 0) {
            subsystem = req.getSoleAttributeAsString(CoreAttributeTypes.Subsystem);
         }
         builtRows[3].append(subsystem);
         Integer counter = summarySubsystemCounter.get(subsystem);
         if (counter == null) {
            counter = new Integer(1);
         } else {
            counter = new Integer(counter.intValue() + 1);
         }
         summarySubsystemCounter.put(subsystem, counter);
         String newline = "";
         boolean foundTrace = false;
         for (ArtifactReadable subSysReq : req.getRelated(CoreRelationTypes.RequirementTrace_LowerLevelRequirement)) {
            builtRows[4].append(newline);
            builtRows[4].append(REQUIREMENT_TRACE_TYPE);
            builtRows[5].append(newline);
            if (subSysReq.getAttributeCount(CoreAttributeTypes.ParagraphNumber) > 0) {
               builtRows[5].append(subSysReq.getSoleAttributeAsString(CoreAttributeTypes.ParagraphNumber));
            }
            builtRows[6].append(newline);
            builtRows[6].append(subSysReq.getName());
            builtRows[7].append(newline);
            builtRows[7].append(subSysReq.getArtifactType().getName());
            List<String> qualificationMethods = subSysReq.getAttributeValues(CoreAttributeTypes.QualificationMethod);
            builtRows[8].append(newline);
            if (qualificationMethods.size() > 0) {
               Iterator<String> iter = qualificationMethods.iterator();
               builtRows[8].append(iter.next());
               while (iter.hasNext()) {
                  builtRows[8].append(iter.next());
               }
            }
            newline = "\n";
            foundTrace = true;
         }
         if (foundTrace) {
            counter = summaryTraceCounter.get(subsystem);
            if (counter == null) {
               counter = new Integer(1);
            } else {
               counter = new Integer(counter.intValue() + 1);
            }
            summaryTraceCounter.put(subsystem, counter);
         }

         foundTrace = false;
         for (ArtifactReadable subSysReq : req.getRelated(CoreRelationTypes.Allocation_Component)) {
            builtRows[4].append(newline);
            builtRows[4].append(ALLOCATION_TRACE_TYPE);
            builtRows[5].append(newline);
            if (subSysReq.getAttributeCount(CoreAttributeTypes.ParagraphNumber) > 0) {
               builtRows[5].append(subSysReq.getSoleAttributeAsString(CoreAttributeTypes.ParagraphNumber));
            }
            builtRows[6].append(newline);
            builtRows[6].append(subSysReq.getName());
            builtRows[7].append(newline);
            builtRows[7].append(subSysReq.getArtifactType().getName());
            List<String> qualificationMethods = subSysReq.getAttributeValues(CoreAttributeTypes.QualificationMethod);
            builtRows[8].append(newline);
            if (qualificationMethods.size() > 0) {
               Iterator<String> iter = qualificationMethods.iterator();
               builtRows[8].append(iter.next());
               while (iter.hasNext()) {
                  builtRows[8].append(iter.next());
               }
            }
            newline = "\n";
            foundTrace = true;
         }
         if (foundTrace) {
            counter = summaryAllocationCounter.get(subsystem);
            if (counter == null) {
               counter = new Integer(1);
            } else {
               counter = new Integer(counter.intValue() + 1);
            }
            summaryAllocationCounter.put(subsystem, counter);
         }
         for (int i = 0; i < 9; i++) {
            row[i] = builtRows[i].toString();
         }
         nullEmptyCells(row);
         writer.writeRow((Object[]) row);
      }
      writer.endSheet();
   }

   private void writeSummarySheet() throws IOException {
      writer.startSheet("Summary", newCol(null, 300), newCol(null, 300, WrappedStyle));

      writer.writeRow("Branch Name", branch.getName());
      writer.writeRow("Report Date", Calendar.getInstance().getTime());
      writer.endRow(); // blank row

      String row[] = new String[2];
      row[0] = "Subsystem";
      row[1] = "Number of System requirements";
      writer.writeRow((Object[]) row);
      for (String subsystem : summarySubsystemCounter.keySet()) {
         row[0] = subsystem;
         Integer counter = summarySubsystemCounter.get(subsystem);
         row[1] = counter.toString();
         writer.writeRow((Object[]) row);
      }
      writer.endRow(); // blank row

      row[0] = "Subsystem";
      row[1] = "Number of System requirements traced to software requirements";
      writer.writeRow((Object[]) row);
      for (String subsystem : summaryTraceCounter.keySet()) {
         row[0] = subsystem;
         Integer counter = summaryTraceCounter.get(subsystem);
         row[1] = counter.toString();
         writer.writeRow((Object[]) row);
      }
      writer.endRow(); // blank row

      row[0] = "Subsystem";
      row[1] = "Number of System requirements traced to allocation components";
      writer.writeRow((Object[]) row);
      for (String subsystem : summaryAllocationCounter.keySet()) {
         row[0] = subsystem;
         Integer counter = summaryAllocationCounter.get(subsystem);
         row[1] = counter.toString();
         writer.writeRow((Object[]) row);
      }

      writer.endSheet();
   }

   private void nullEmptyCells(String[] row) {
      for (int i = 0; i < row.length; i++) {
         String trimmed = row[i].trim().replaceAll("\n", "");
         if (!Strings.isValid(trimmed)) {
            row[i] = null;
         }
      }
   }

   private Collection<ArtifactTypeToken> convertStringTypes(String csvTypes) {
      if (allTypesMap.isEmpty()) {
         ArtifactTypes artifactTypes = types.getArtifactTypes();
         for (ArtifactTypeToken type : artifactTypes.getAll()) {
            allTypesMap.put(type.getName(), type);
         }
      }
      StringTokenizer parser = new StringTokenizer(csvTypes, ",");

      ArrayList<ArtifactTypeToken> theReturn = new ArrayList<>();
      while (parser.hasMoreTokens()) {
         ArtifactTypeToken type = allTypesMap.get(parser.nextToken());
         if (type != null) {
            theReturn.add(type);
         }
      }
      if (theReturn.isEmpty()) {
         return null;
      }
      return theReturn;
   }
}