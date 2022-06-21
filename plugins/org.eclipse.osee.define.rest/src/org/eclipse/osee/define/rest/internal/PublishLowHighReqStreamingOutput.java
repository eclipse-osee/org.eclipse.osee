/*********************************************************************
 * Copyright (c) 2014 Boeing
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
import org.eclipse.osee.define.api.OseeHierarchyComparator;
import org.eclipse.osee.define.api.ParagraphNumberComparator;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.BranchQuery;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author Marc Potter
 */

public final class PublishLowHighReqStreamingOutput implements StreamingOutput {
   private final QueryFactory queryApi;
   private final OrcsTokenService tokenService;
   private final BranchToken branch;
   private final ActivityLog activityLog;
   private final Map<String, Integer> summarySubsystemCounter = new HashMap<>();
   private final Map<String, Integer> summaryTraceCounter = new HashMap<>();
   private ExcelXmlWriter writer;
   private final Collection<ArtifactTypeToken> includeOnlyArtifactTypes;
   private final String REQUIREMENT_TRACE_TYPE = "Requirement Trace";
   private final Map<String, ArtifactTypeToken> allTypesMap = new HashMap<>();
   private static final AttributeTypeToken SRSName = AttributeTypeToken.valueOf(44074994010072549L, "SRS Name");
   private OseeHierarchyComparator hierarchyComparator = null;

   public PublishLowHighReqStreamingOutput(ActivityLog activityLog, OrcsApi orcsApi, BranchId branch, String selectedTypes) {
      this.activityLog = activityLog;
      this.queryApi = orcsApi.getQueryFactory();
      BranchQuery query = orcsApi.getQueryFactory().branchQuery();
      this.branch = query.andId(branch).getResultsAsId().getExactlyOne();
      this.tokenService = orcsApi.tokenService();
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

      writer.startSheet("Low to High Level Req", newCol(null, 85, WrappedStyle), newCol(null, 400), newCol(null, 170),
         newCol(null, 85, WrappedStyle), newCol(null, 120, WrappedStyle), newCol(null, 85, WrappedStyle),
         newCol(null, 85, WrappedStyle), newCol(null, 400, WrappedStyle), newCol(null, 350, WrappedStyle),
         newCol(null, 250, WrappedStyle));

      QueryBuilder query = queryApi.fromBranch(branch).andIsOfType(CoreArtifactTypes.AbstractSoftwareRequirement);
      String[] row =
         {"Low Level Requirement", null, null, null, null, "High Level Requirement", null, null, null, null};
      writer.writeRow((Object[]) row);
      row[0] = "Paragraph #";
      row[1] = "Req Name";
      row[2] = "Requirement Type";
      row[3] = "Sw CI Level";
      row[4] = "Qualification Method";
      row[5] = "System Spec ID";
      row[6] = "Paragraph #";
      row[7] = "Req Name";
      row[8] = "Requirement Type";
      row[9] = "Subsystem";
      writer.writeRow((Object[]) row);

      if (includeOnlyArtifactTypes == null) {
         // nothing selected
         writer.endSheet();
         return;
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
         for (int i = 0; i < 10; i++) {
            row[i] = "";
         }
         if (req.getAttributeCount(CoreAttributeTypes.ParagraphNumber) > 0) {
            row[0] = req.getSoleAttributeAsString(CoreAttributeTypes.ParagraphNumber);
         }
         row[1] = req.getName();
         row[2] = req.getArtifactType().getName();
         if (req.getAttributeCount(CoreAttributeTypes.Category) > 0) {
            row[3] = req.getSoleAttributeAsString(CoreAttributeTypes.Category);
         }
         List<String> qualificationMethods = req.getAttributeValues(CoreAttributeTypes.QualificationMethod);
         if (qualificationMethods.size() > 0) {
            Iterator<String> iter = qualificationMethods.iterator();
            row[4] = iter.next();
            while (iter.hasNext()) {
               row[4] += ", ";
               row[4] += iter.next();
            }
         } else {
            row[4] = "";
         }
         ResultSet<ArtifactReadable> subSystemRequirements =
            req.getRelated(CoreRelationTypes.RequirementTrace_HigherLevelRequirement);
         if (subSystemRequirements.size() < 1) {
            for (int i = 0; i < 10; i++) {
               if (Strings.isInValid(row[i])) {
                  row[i] = " ";
               }
            }
            writer.writeRow((Object[]) row);
         } else {
            for (ArtifactReadable subSysReq : subSystemRequirements) {

               if (subSysReq.getAttributeCount(CoreAttributeTypes.LegacyId) > 0) {
                  row[5] = subSysReq.getSoleAttributeAsString(CoreAttributeTypes.LegacyId);
               }

               try {
                  row[6] = subSysReq.getSoleAttributeAsString(CoreAttributeTypes.ParagraphNumber);
               } catch (Exception ex) {
                  row[6] = "Paragraph # unavailable";
               }

               row[7] = subSysReq.getName();

               row[8] = subSysReq.getArtifactType().getName();
               String subsystem = "Not specified";
               if (subSysReq.getAttributeCount(CoreAttributeTypes.Subsystem) > 0) {
                  subsystem = subSysReq.getSoleAttributeAsString(CoreAttributeTypes.Subsystem);
               }

               row[9] = subsystem;
               for (int i = 0; i < 10; i++) {
                  if (Strings.isInValid(row[i])) {
                     row[i] = " ";
                  }
               }
               writer.writeRow((Object[]) row);
               for (int i = 0; i < 10; ++i) {
                  row[i] = "";
               }
            }
         }
      }

      writer.endSheet();
   }

   private void writeHighLowTraceSheet() throws IOException {

      writer.startSheet("High to Low Level Req", newCol(null, 85, WrappedStyle), newCol(null, 85), newCol(null, 400),
         newCol(null, 150), newCol(null, 120, WrappedStyle), newCol(null, 120, WrappedStyle),
         newCol(null, 85, WrappedStyle), newCol(null, 400, WrappedStyle), newCol(null, 85, WrappedStyle),
         newCol(null, 350, WrappedStyle), newCol(null, 250, WrappedStyle));

      QueryBuilder query = queryApi.fromBranch(branch).andIsOfType(CoreArtifactTypes.AbstractSystemRequirement);
      String[] row =
         {"Higher Level Requirement", null, null, null, null, null, "Low Level Requirement", null, null, null, null};
      writer.writeRow((Object[]) row);
      row[0] = "System Spec ID";
      row[1] = "Paragraph #";
      row[2] = "Req Name";
      row[3] = "Requirement Type";
      row[4] = "Subsystem";
      row[5] = "Relation Type";
      row[6] = "SwCI Level";
      row[7] = "Paragraph #";
      row[8] = "Req Name";
      row[9] = "Requirement Type";
      row[10] = "Qualification Method";
      writer.writeRow((Object[]) row);

      for (ArtifactReadable req : query.getResults().sort(new ParagraphNumberComparator(activityLog))) {
         for (int i = 0; i < 11; i++) {
            row[i] = "";
         }
         if (req.getAttributeCount(CoreAttributeTypes.LegacyId) > 0) {
            row[0] = req.getSoleAttributeAsString(CoreAttributeTypes.LegacyId);
         }
         if (req.getAttributeCount(CoreAttributeTypes.ParagraphNumber) > 0) {
            row[1] = req.getSoleAttributeAsString(CoreAttributeTypes.ParagraphNumber);
         }
         row[2] = req.getName();
         row[3] = req.getArtifactType().getName();
         String subsystem = "Not specified";
         if (req.getAttributeCount(CoreAttributeTypes.Subsystem) > 0) {
            subsystem = req.getSoleAttributeAsString(CoreAttributeTypes.Subsystem);
         }
         row[4] = subsystem;
         Integer counter = summarySubsystemCounter.get(subsystem);
         if (counter == null) {
            counter = Integer.valueOf(1);
         } else {
            counter = Integer.valueOf(counter.intValue() + 1);
         }
         summarySubsystemCounter.put(subsystem, counter);
         ResultSet<ArtifactReadable> subSystemRequirements =
            req.getRelated(CoreRelationTypes.RequirementTrace_LowerLevelRequirement);
         if (subSystemRequirements.size() < 1) {
            for (int i = 0; i < 11; i++) {
               if (Strings.isInValid(row[i])) {
                  row[i] = " ";
               }
            }
            writer.writeRow((Object[]) row);
         } else {
            boolean foundTrace = false;
            for (ArtifactReadable subSysReq : subSystemRequirements) {

               row[5] = REQUIREMENT_TRACE_TYPE;

               if (subSysReq.getAttributeCount(CoreAttributeTypes.Category) > 0) {
                  row[6] = subSysReq.getSoleAttributeAsString(CoreAttributeTypes.Category);
               }

               if (subSysReq.getAttributeCount(CoreAttributeTypes.ParagraphNumber) > 0) {
                  row[7] = subSysReq.getSoleAttributeAsString(CoreAttributeTypes.ParagraphNumber);
               }
               if (checkName(subSysReq.getArtifactType().getName())) {
                  fixupParagraphNumber(row, subSysReq);
                  row[8] = subSysReq.getSoleAttributeAsString(SRSName);
               } else {
                  row[8] = subSysReq.getName();
               }

               row[9] = subSysReq.getArtifactType().getName();
               List<String> qualificationMethods = subSysReq.getAttributeValues(CoreAttributeTypes.QualificationMethod);

               if (qualificationMethods.size() > 0) {
                  Iterator<String> iter = qualificationMethods.iterator();
                  row[10] = iter.next();
                  while (iter.hasNext()) {
                     row[10] += ", ";
                     row[10] += iter.next();
                  }
               }
               foundTrace = true;
               for (int i = 0; i < 11; i++) {
                  if (Strings.isInValid(row[i])) {
                     row[i] = " ";
                  }
               }
               writer.writeRow((Object[]) row);
               for (int i = 0; i < 11; ++i) {
                  row[i] = "";
               }
            }
            if (foundTrace) {
               counter = summaryTraceCounter.get(subsystem);
               if (counter == null) {
                  counter = Integer.valueOf(1);
               } else {
                  counter = Integer.valueOf(counter.intValue() + 1);
               }
               summaryTraceCounter.put(subsystem, counter);
            }
         }
      }
      writer.endSheet();
   }

   private void fixupParagraphNumber(String[] row, ArtifactReadable subSysReq) {
      if (hierarchyComparator == null) {
         hierarchyComparator = new OseeHierarchyComparator(activityLog);
         ArtifactReadable grandParent = subSysReq.getParent().getParent();
         List<ArtifactReadable> children =
            queryApi.fromBranch(branch).andRelatedRecursive(CoreRelationTypes.DefaultHierarchical_Child,
               grandParent).getResults().getList();
         children.sort(hierarchyComparator);
      }
      if (Strings.isInValid(row[7])) {
         row[7] = convertParagraphNumber(hierarchyComparator.getHierarchyPosition(subSysReq));
      }
   }

   private String convertParagraphNumber(String given) {
      if (given.startsWith("6.9.2")) {
         // warning
         return given.replaceFirst("6\\.9\\.2", "B\\.1");
      }
      if (given.startsWith("6.9.3")) {
         // caution
         return given.replaceFirst("6\\.9\\.3", "B\\.2");
      }
      if (given.startsWith("6.9.4")) {
         // advisory
         return given.replaceFirst("6\\.9\\.4", "B\\.3");
      }
      return "";
   }

   private boolean checkName(String name) {
      if ("Warning".equals(name)) {
         return true;
      }
      if ("Caution".equals(name)) {
         return true;
      }
      if ("Advisory".equals(name)) {
         return true;
      }
      if ("Fault".equals(name)) {
         return true;
      }
      if ("Exceedance".equals(name)) {
         return true;
      }
      return false;
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

      writer.endSheet();
   }

   private Collection<ArtifactTypeToken> convertStringTypes(String csvTypes) {
      if (allTypesMap.isEmpty()) {
         for (ArtifactTypeToken type : tokenService.getArtifactTypes()) {
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