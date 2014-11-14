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
package org.eclipse.osee.disposition.rest.internal.importer.coverage;

import static org.eclipse.osee.disposition.model.DispoStrings.Exception_Handling_Resolution;
import static org.eclipse.osee.disposition.model.DispoStrings.Test_Unit_Resolution;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.rest.DispoImporterApi;
import org.eclipse.osee.disposition.rest.internal.DispoConnector;
import org.eclipse.osee.disposition.rest.internal.DispoDataFactory;
import org.eclipse.osee.disposition.rest.internal.importer.AnnotationCopier;
import org.eclipse.osee.disposition.rest.util.DispoUtil;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.vcast.VCastClient;
import org.eclipse.osee.vcast.VCastDataStore;
import org.eclipse.osee.vcast.VCastLisFileParser;
import org.eclipse.osee.vcast.VCastValidateDatFileSyntax;
import org.eclipse.osee.vcast.model.VCastFunction;
import org.eclipse.osee.vcast.model.VCastInstrumentedFile;
import org.eclipse.osee.vcast.model.VCastResult;
import org.eclipse.osee.vcast.model.VCastSourceFileJoin;
import org.eclipse.osee.vcast.model.VCastStatementCoverage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Angel Avila
 */
public class LisFileParser implements DispoImporterApi {
   private final DispoDataFactory dataFactory;

   private static final Pattern fileMethodLineNumberPattern = Pattern.compile("\\s*([0-9]+)\\s+([0-9]+)\\s+([0-9]+)");
   private final Map<String, DispoItemData> datIdToItem = new HashMap<String, DispoItemData>();
   private final Set<String> datIdsCoveredByException = new HashSet<String>();
   private final Set<String> alreadyUsedDatIds = new HashSet<String>();

   private final DispoConnector dispoConnector = new DispoConnector();

   private String vCastDir;

   public LisFileParser(Log logger, DispoDataFactory dataFactory) {
      this.dataFactory = dataFactory;
   }

   @Override
   public List<DispoItem> importDirectory(Map<String, DispoItem> exisitingItems, File filesDir) {
      List<DispoItem> toReturn = new ArrayList<DispoItem>();

      vCastDir = filesDir.getAbsolutePath() + File.separator + "vcast";
      File f = new File(vCastDir + File.separator + "cover.db");

      VCastDataStore dataStore = VCastClient.newDataStore(f.getAbsolutePath());

      Collection<VCastInstrumentedFile> instrumentedFiles = getInstrumentedFiles(dataStore);

      for (VCastInstrumentedFile instrumentedFile : instrumentedFiles) {
         try {
            processInstrumented(dataStore, instrumentedFile);
         } catch (Exception ex) {
            //
         }
      }

      Collection<VCastResult> results = getResultFiles(dataStore);
      for (VCastResult result : results) {
         try {
            processResult(result);
         } catch (Exception ex) {
            //
         }
      }

      try {
         processExceptionHandled();
      } catch (JSONException ex) {
         //
      }

      Collection<DispoItemData> values = datIdToItem.values();

      for (DispoItemData item : values) {
         if (item.getName().equals("air_data_cross_monitor.2.LIS.<<package-processing>>")) {
            System.out.println("");
         }
         dataFactory.initDispoItem(item);
         item.setTotalPoints(String.valueOf(item.getAnnotationsList().length() + item.getDiscrepanciesList().length()));
      }

      // This is a reimport so we'll need to copy all the annotations
      if (!exisitingItems.isEmpty()) {
         AnnotationCopier copier = new AnnotationCopier(dispoConnector);
         try {
            List<DispoItemData> itemsFromImport = new ArrayList<DispoItemData>();
            itemsFromImport.addAll(values);

            toReturn = copier.copyEntireSet(itemsFromImport, exisitingItems.values(), false);
         } catch (JSONException ex) {
            //
         }
      } else {
         toReturn = new ArrayList<DispoItem>();
         toReturn.addAll(values);
      }
      return toReturn;
   }

   private void processExceptionHandled() throws JSONException {
      for (String datId : datIdsCoveredByException) {
         Matcher matcher = Pattern.compile("\\d*:\\d*:").matcher(datId);
         matcher.find();
         String itemDatId = matcher.group();
         DispoItemData item = datIdToItem.get(itemDatId);

         String line = datId.replaceAll("\\d*:\\d*:", "");
         line = line.replaceAll(":", "");
         addAnnotationForForCoveredLine(item, line, Exception_Handling_Resolution, "");
         removeDisrepancy(line, item.getDiscrepanciesList());
      }
   }

   private Collection<VCastInstrumentedFile> getInstrumentedFiles(VCastDataStore dataStore) {
      Collection<VCastInstrumentedFile> instrumentedFiles = new ArrayList<VCastInstrumentedFile>();
      try {
         /**
          * Note: the LIS_file field of the instrumentedFiles may have a fictious absolute path - but the path is
          * ignored and only the file name is used.
          */
         instrumentedFiles = dataStore.getAllInstrumentedFiles();
      } catch (OseeCoreException ex) {
         throw new OseeCoreException(ex, "SQL error while reading functions for directory: ", vCastDir);
      }
      return instrumentedFiles;
   }

   private void processInstrumented(VCastDataStore dataStore, VCastInstrumentedFile instrumentedFile) throws Exception {
      VCastSourceFileJoin sourceFile = null;
      try {
         sourceFile = dataStore.getSourceFileJoin(instrumentedFile);
      } catch (OseeCoreException ex) {
         throw new OseeCoreException(ex, "SQL error while reading source_files for instrumented_file id:%s",
            instrumentedFile.getId());
      }

      if (sourceFile != null) {
         int fileNum = sourceFile.getUnitIndex();

         String lisFileName_badPath = instrumentedFile.getLISFile();
         if (!Strings.isValid(lisFileName_badPath)) {
            // Log error here
         }

         String normalizedPath = lisFileName_badPath.replaceAll("\\\\", "/");
         File f = new File(normalizedPath);
         String lisFileName = f.getName();
         VCastLisFileParser lisFileParser = new VCastLisFileParser(lisFileName, vCastDir);

         Collection<VCastFunction> functions = Collections.emptyList();
         try {
            functions = dataStore.getFunctions(instrumentedFile);
         } catch (OseeCoreException ex) {
            throw new OseeCoreException(ex, "SQL error while reading functions for instrumented_file id: %d",
               instrumentedFile.getId());
         }

         for (VCastFunction function : functions) {
            processFunction(lisFileName, lisFileParser, fileNum, dataStore, instrumentedFile, function);
         }
      }
   }

   private void processFunction(String lisFileName, VCastLisFileParser lisFileParser, int fileNum, VCastDataStore dataStore, VCastInstrumentedFile instrumentedFile, VCastFunction function) {
      int functionNum = function.getFindex();
      DispoItemData newItem = new DispoItemData();
      newItem.setAnnotationsList(new JSONArray());
      newItem.setName(lisFileName + "." + function.getName());

      String datId = generateDatId(fileNum, functionNum);
      datIdToItem.put(datId, newItem);

      Collection<VCastStatementCoverage> statementCoverageItems = Collections.emptyList();

      try {
         statementCoverageItems = dataStore.getStatementCoverageLines(function);
      } catch (OseeCoreException ex) {
         throw new OseeCoreException(ex, "SQL error while reading functions for instrumented_file id: %d",
            instrumentedFile.getId());
      }

      Map<String, JSONObject> discrepancies = new HashMap<String, JSONObject>();

      for (VCastStatementCoverage statementCoverageItem : statementCoverageItems) {
         processStatement(lisFileName, lisFileParser, fileNum, functionNum, function, statementCoverageItem,
            discrepancies);
      }

      newItem.setDiscrepanciesList(new JSONObject(discrepancies));
      // add discrepancies to item
   }

   private void processStatement(String lisFileName, VCastLisFileParser lisFileParser, int fileNum, int functionNum, VCastFunction function, VCastStatementCoverage statementCoverageItem, Map<String, JSONObject> discrepancies) {
      // Create discrepancy for every line, annotate with test usnit or exception handled
      Integer functionNumber = function.getFindex();
      Integer lineNumber = statementCoverageItem.getLine();
      Pair<String, Boolean> lineData = null;

      Discrepancy newDiscrepancy = new Discrepancy();

      try {
         lineData = lisFileParser.getSourceCodeForLine(functionNumber, lineNumber);
      } catch (Exception ex) {
         throw new OseeCoreException(ex, "Error parsing LIS file: [%s], on function [%s]", lisFileName,
            function.getName());
      }

      if (lineData != null) {
         newDiscrepancy.setText(lineData.getFirst().trim());
         newDiscrepancy.setLocation(lineNumber);
         String id = String.valueOf(Lib.generateUuid());
         newDiscrepancy.setId(id);
         discrepancies.put(id, new JSONObject(newDiscrepancy));

         // Is covered by exception handling, pass as parameter from DispoApiImpl
         if (lineData.getSecond()) {
            String datId = generateDatId(fileNum, functionNum, lineNumber);
            datIdsCoveredByException.add(datId);
         }

      }
   }

   private String generateDatId(Object... ids) {
      StringBuilder sb = new StringBuilder();
      for (Object id : ids) {
         sb.append(id);
         sb.append(":");
      }

      return sb.toString();
   }

   private void processResult(VCastResult result) throws Exception {
      String resultPath = result.getPath();

      String resultPathAbs = vCastDir + File.separator + resultPath;

      File resultsFile = new File(resultPathAbs);
      if (!resultsFile.exists()) {
         // Log missing dat file
      } else {
         //maybe create import record and log this Dat file
         //Start reading line by line
         BufferedReader br = null;
         try {
            br = new BufferedReader(new FileReader(resultsFile));
            String resultsLine;
            while ((resultsLine = br.readLine()) != null) {

               // Loop through results file and log coverageItem as Test_Unit for each entry
               if (Strings.isValid(resultsLine)) {
                  Result datFileSyntaxResult = VCastValidateDatFileSyntax.validateDatFileSyntax(resultsLine);
                  if (!datFileSyntaxResult.isTrue()) {
                     // Log
                  } else {
                     Matcher m = fileMethodLineNumberPattern.matcher(resultsLine);
                     if (m.find()) {
                        if (!alreadyUsedDatIds.contains(resultsLine)) {
                           DispoItemData item = datIdToItem.get(generateDatId(m.group(1), m.group(2)));
                           String location = m.group(3);
                           addAnnotationForForCoveredLine(item, location, Test_Unit_Resolution, resultPath);
                           removeDisrepancy(location, item.getDiscrepanciesList());
                           alreadyUsedDatIds.add(resultsLine);
                        }
                     }
                  }
               }
            }
         } finally {
            Lib.close(br);
         }
      }
   }

   private void removeDisrepancy(String location, JSONObject discrepancies) throws JSONException {
      @SuppressWarnings("unchecked")
      Iterator<String> iterator = discrepancies.keys();
      while (iterator.hasNext()) {
         String key = iterator.next();
         JSONObject discrepancyAsJson = discrepancies.getJSONObject(key);
         Discrepancy discrepancy = DispoUtil.jsonObjToDiscrepancy(discrepancyAsJson);
         if (String.valueOf(discrepancy.getLocation()).equals(location)) {
            discrepancies.remove(key);
            break;
         }
      }

   }

   private void addAnnotationForForCoveredLine(DispoItemData item, String location, String resolutionType, String coveringFile) throws JSONException {
      DispoAnnotationData newAnnotation = new DispoAnnotationData();
      dataFactory.initAnnotation(newAnnotation);
      String idOfNewAnnotation = dataFactory.getNewId();
      newAnnotation.setId(idOfNewAnnotation);

      newAnnotation.setIsDefault(true);
      newAnnotation.setLocationRefs(location);
      newAnnotation.setResolutionType(resolutionType);
      newAnnotation.setResolution(coveringFile);
      newAnnotation.setIsResolutionValid(true);
      dispoConnector.connectAnnotation(newAnnotation, item.getDiscrepanciesList());

      JSONArray annotationsList = item.getAnnotationsList();
      int newIndex = annotationsList.length();
      newAnnotation.setIndex(newIndex);
      annotationsList.put(newIndex, DispoUtil.annotationToJsonObj(newAnnotation));
   }

   private Collection<VCastResult> getResultFiles(VCastDataStore dataStore) {
      Collection<VCastResult> results = null;
      results = dataStore.getAllResults();
      return results;
   }
}
