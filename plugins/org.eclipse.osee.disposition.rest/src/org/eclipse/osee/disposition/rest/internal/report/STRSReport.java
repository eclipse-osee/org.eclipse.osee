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
package org.eclipse.osee.disposition.rest.internal.report;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;

import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoProgram;
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.disposition.rest.internal.DispoConnector;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;

/**
 * @author Angel Avila
 */

public class STRSReport {
   private final DispoApi dispoApi;

   public STRSReport(DispoApi dispoApi) {
      this.dispoApi = dispoApi;
   }

   public void runReport(DispoProgram program, DispoSet setPrimary, DispoSet setSecondary, OutputStream outputStream) {
      List<DispoItem> itemsFromPrimary = dispoApi.getDispoItems(program, setPrimary.getGuid(), true);
      List<DispoItem> itemsFromSecondary = dispoApi.getDispoItems(program, setSecondary.getGuid(), true);

      HashMap<String, DispoItem> idsToDryRun = convertToMap(itemsFromSecondary);

      try {
         Writer writer = new OutputStreamWriter(outputStream, "UTF-8");
         ExcelXmlWriter sheetWriter = new ExcelXmlWriter(writer);

         String[] headers = getHeaders();
         int columns = headers.length;
         sheetWriter.startSheet("STRS Report", headers.length);
         sheetWriter.writeRow((Object[]) headers);

         for (DispoItem demoItem : itemsFromPrimary) {
            DispoConnector connector = new DispoConnector();
            List<String> allUncoveredDiscprepancies = connector.getAllUncoveredDiscprepancies(demoItem);
            String[] row = new String[columns];
            int index = 0;

            DispoItem dryrunItem = idsToDryRun.get(demoItem.getName());
            List<DispoAnnotationData> annotationsList = demoItem.getAnnotationsList();
            HashMap<String, Integer> issueTypeToCount = convertToIssueTypeToCounttMap(annotationsList);

            row[index++] = String.valueOf(demoItem.getName());
            if (dryrunItem != null) {
               row[index++] = String.valueOf(dryrunItem.getTotalPoints());
               row[index++] = String.valueOf(dryrunItem.getDiscrepanciesList().size());
            } else {
               row[index++] = "No corresponding Item";
               row[index++] = "No corresponding Item";
            }
            row[index++] = String.valueOf(demoItem.getTotalPoints());
            row[index++] = String.valueOf(issueTypeToCount.get("CODE"));
            row[index++] = String.valueOf(issueTypeToCount.get("SCRIPT"));
            row[index++] = String.valueOf(issueTypeToCount.get("REQ"));
            row[index++] = String.valueOf(allUncoveredDiscprepancies.size());
            row[index++] = String.valueOf(issueTypeToCount.get("OTHER"));
            row[index++] = String.valueOf(demoItem.getDiscrepanciesList().size());
            row[index++] = " ";
            row[index++] = String.valueOf(getCustomerNotes(demoItem.getAnnotationsList()));

            sheetWriter.writeRow((Object[]) row);
         }

         sheetWriter.endSheet();
         sheetWriter.endWorkbook();

      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }

   private HashMap<String, DispoItem> convertToMap(List<DispoItem> list) {
      HashMap<String, DispoItem> toReturn = new HashMap<>();
      for (DispoItem item : list) {
         toReturn.put(item.getName(), item);
      }

      return toReturn;
   }

   private String getCustomerNotes(List<DispoAnnotationData> annotations) {
      StringBuilder sb = new StringBuilder();
      for (DispoAnnotationData annotation : annotations) {
         String customerNotes = annotation.getCustomerNotes();
         if (!customerNotes.equalsIgnoreCase("--Enter Notes--")) {
            sb.append(annotation.getCustomerNotes());
            sb.append("\n");
         }
      }
      if (sb.length() == 0) {
         sb.append(" ");
      }
      return sb.toString();
   }

   private static String[] getHeaders() {
      String[] toReturn = {
         "Test Case",
         "Dry Run Results:Test Points",
         "Dry Run Results:Total Fails",
         "DemoResults:Total Test Points",
         "Demo: Code Problems",
         "Script Problems",
         "Requirements",
         "Under Investigation",
         "Other",
         "Total Fails",
         "Verification of RPCR",
         "Commnets"};
      return toReturn;
   }

   private HashMap<String, Integer> convertToIssueTypeToCounttMap(List<DispoAnnotationData> annotationList) {
      HashMap<String, Integer> toReturn = new HashMap<>();
      int codeCount = 0;
      int scriptCount = 0;
      int reqCount = 0;
      int other = 0;
      for (DispoAnnotationData annotation : annotationList) {
         if (annotation.isValid()) {
            String resolutionType = annotation.getResolutionType();
            if (resolutionType != null) {
               if (resolutionType.equalsIgnoreCase("CODE")) {
                  codeCount += getTotalLocationOfAnnotation(annotation);
               } else if (resolutionType.equalsIgnoreCase("TEST")) {
                  scriptCount += getTotalLocationOfAnnotation(annotation);
               } else if (resolutionType.equalsIgnoreCase("REQ")) {
                  reqCount += getTotalLocationOfAnnotation(annotation);
               } else {
                  other += getTotalLocationOfAnnotation(annotation);
               }
            }
         }
      }

      toReturn.put("CODE", new Integer(codeCount));
      toReturn.put("SCRIPT", new Integer(scriptCount));
      toReturn.put("REQ", new Integer(reqCount));
      toReturn.put("OTHER", new Integer(other));

      return toReturn;
   }

   private int getTotalLocationOfAnnotation(DispoAnnotationData annotation) {
      String locationRefs = annotation.getLocationRefs();
      String[] locationsRefsArray = locationRefs.split(",");
      int toReturn = 0;
      for (int i = 0; i < locationsRefsArray.length; i++) {
         String singleRef = locationsRefsArray[i].trim();
         if (singleRef.contains("-")) {
            String[] split = singleRef.split("-");
            int gap = Integer.valueOf(split[1]) - Integer.valueOf(split[0]);
            toReturn += gap + 1; //add one since the gap is 1 less than what the range covers. i.e. 3-6 gap is 3 but covers 4
         } else {
            toReturn++;
         }
      }
      return toReturn;
   }
}
