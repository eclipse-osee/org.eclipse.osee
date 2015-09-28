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
package org.eclipse.osee.disposition.rest.internal.report;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoProgram;
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.disposition.rest.internal.DispoConnector;
import org.eclipse.osee.disposition.rest.internal.LocationRangesCompressor;
import org.eclipse.osee.disposition.rest.util.DispoUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Angel Avila
 */
public class ExportSet {
   private final DispoApi dispoApi;

   public ExportSet(DispoApi dispoApi) {
      this.dispoApi = dispoApi;
   }

   public void runReport(DispoProgram program, DispoSet setPrimary, String option, OutputStream outputStream) {
      List<DispoItem> items = dispoApi.getDispoItems(program, setPrimary.getGuid());

      try {
         Writer writer = new OutputStreamWriter(outputStream, "UTF-8");
         ExcelXmlWriter sheetWriter = new ExcelXmlWriter(writer);

         String[] headers = getHeadersDetailed();
         int columns = headers.length;
         sheetWriter.startSheet(setPrimary.getName(), headers.length);
         sheetWriter.writeRow((Object[]) headers);

         for (DispoItem item : items) {
            DispoConnector connector = new DispoConnector();
            List<Integer> allUncoveredDiscprepancies = connector.getAllUncoveredDiscprepancies(item);
            String[] row = new String[columns];
            int index = 0;

            JSONObject discrepanciesList = item.getDiscrepanciesList();

            row[index++] = String.valueOf(item.getName());
            row[index++] = String.valueOf(item.getCategory());
            row[index++] = String.valueOf(item.getStatus());
            row[index++] = String.valueOf(item.getTotalPoints());
            row[index++] = String.valueOf(item.getDiscrepanciesList().length());
            row[index++] = String.valueOf(DispoUtil.discrepanciesToString(discrepanciesList));
            row[index++] = String.valueOf(allUncoveredDiscprepancies.size());
            row[index++] = String.valueOf(LocationRangesCompressor.compress(allUncoveredDiscprepancies));
            row[index++] = String.valueOf(item.getAssignee());
            row[index++] = String.valueOf(item.getItemNotes());
            row[index++] = String.valueOf(item.getNeedsRerun());
            row[index++] = String.valueOf(item.getAborted());
            row[index++] = String.valueOf(item.getMachine());
            row[index++] = String.valueOf(item.getElapsedTime());
            row[index++] = String.valueOf(item.getCreationDate());
            row[index++] = String.valueOf(item.getLastUpdate());
            row[index++] = String.valueOf(item.getVersion());
            row[index++] = String.valueOf(prettifyAnnotations(item.getAnnotationsList()));

            sheetWriter.writeRow((Object[]) row);
         }

         sheetWriter.endSheet();
         sheetWriter.endWorkbook();

      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }

   }

   public void runCoverageReport(DispoProgram program, DispoSet setPrimary, String option, OutputStream outputStream) {
      List<DispoItem> items = dispoApi.getDispoItems(program, setPrimary.getGuid());

      try {
         Writer writer = new OutputStreamWriter(outputStream, "UTF-8");
         ExcelXmlWriter sheetWriter = new ExcelXmlWriter(writer);

         String[] headers = getHeadersCoverage();
         int columns = headers.length;
         sheetWriter.startSheet(setPrimary.getName(), headers.length);
         sheetWriter.writeRow((Object[]) headers);

         for (DispoItem item : items) {
            Map<String, DispoAnnotationData> idToAnnotations = getDiscrepancyIdToCoveringAnnotation(item);
            JSONObject discrepanciesList = item.getDiscrepanciesList();
            @SuppressWarnings("rawtypes")
            Iterator keys = discrepanciesList.keys();
            while (keys.hasNext()) {
               String key = (String) keys.next();
               Discrepancy discrepancy = DispoUtil.jsonObjToDiscrepancy(discrepanciesList.getJSONObject(key));
               DispoAnnotationData coveringAnnotation = idToAnnotations.get(discrepancy.getId());
               if (coveringAnnotation == null) {
                  coveringAnnotation = createUncoveredAnnotation();
               }

               writeRow(sheetWriter, columns, item, discrepancy, coveringAnnotation);

            }
         }

         sheetWriter.endSheet();
         sheetWriter.endWorkbook();
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }

   }

   private void writeRow(ExcelXmlWriter sheetWriter, int columns, DispoItem item, Discrepancy discrepancy, DispoAnnotationData annoation) throws IOException {

      String[] row = new String[columns];
      int index = 0;
      row[index++] = "Empty";
      row[index++] = item.getName();
      //      row[index++] = item.getName();
      row[index++] = discrepancy.getText();
      row[index++] = String.valueOf(item.getMethodNumber());
      row[index++] = String.valueOf(discrepancy.getLocation());
      row[index++] = annoation.getResolutionType();
      row[index++] = annoation.getResolution();
      sheetWriter.writeRow((Object[]) row);
   }

   private Map<String, DispoAnnotationData> getDiscrepancyIdToCoveringAnnotation(DispoItem item) throws JSONException {
      Map<String, DispoAnnotationData> toReturn = new HashMap<>();
      JSONArray annotationsList = item.getAnnotationsList();
      for (int i = 0; i < annotationsList.length(); i++) {
         DispoAnnotationData annotation = DispoUtil.jsonObjToDispoAnnotationData(annotationsList.getJSONObject(i));
         JSONArray idsOfCoveredDiscrepancies = annotation.getIdsOfCoveredDiscrepancies();

         if (idsOfCoveredDiscrepancies.length() > 0) {
            toReturn.put(idsOfCoveredDiscrepancies.getString(0), annotation);
         }
      }
      return toReturn;
   }

   private static DispoAnnotationData createUncoveredAnnotation() {
      DispoAnnotationData annotation = new DispoAnnotationData();
      annotation.setResolutionType("Uncovered");
      annotation.setResolution("N/A");
      return annotation;
   }

   private static String prettifyAnnotations(JSONArray annotations) throws JSONException {
      StringBuilder sb = new StringBuilder();

      for (int i = 0; i < annotations.length(); i++) {
         JSONObject annotationJson = annotations.getJSONObject(i);
         DispoAnnotationData annotation = DispoUtil.jsonObjToDispoAnnotationData(annotationJson);
         sb.append(annotation.getLocationRefs());
         sb.append(":");
         sb.append(annotation.getResolution());
         sb.append("\n");
      }

      return sb.toString();
   }

   private static String[] getHeadersDetailed() {
      String[] toReturn = {//
         "Script Name",//
         "Category",//
         "Status",//
         "Total Test Points",//
         "Failures",//
         "Failed Points",//
         "Remaining Count",//
         "Remaining Points",//
         "Assignee",//
         "Item Notes",//
         "Needs Rerun",//
         "Aborted",//
         "Station",//
         "Elapsed Time",//
         "Creation Date",//
         "Last Updated",//
         "Version",//
         "Dispositions"//
      };
      return toReturn;
   }

   private static String[] getHeadersCoverage() {
      String[] toReturn = {//
         "Namespace",//
         "Parent Coverage Unit",//
         "Unit",//
         "Method Number",//
         "Execution Line Number",//
         "Coverage Method",//
      "Coverage Rationale"};
      return toReturn;
   }
}
