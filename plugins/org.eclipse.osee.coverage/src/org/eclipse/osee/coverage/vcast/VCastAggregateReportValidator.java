/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.vcast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.coverage.model.CoverageOptionManager;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class VCastAggregateReportValidator {

   Pattern coverageUnitPattern = Pattern.compile("Code&nbsp;Coverage&nbsp;for&nbsp;Unit:&nbsp;(.*?)<");
   Pattern resultsPattern = Pattern.compile("&nbsp;([0-9]+)&nbsp;of&nbsp;([0-9]+)&nbsp;Lines&nbsp;Covered&nbsp;");
   String NO_COVERAGE_DATA_EXISTS = "No&nbsp;Coverage&nbsp;Data&nbsp;Exists";
   boolean error = false;
   private VectorCastAdaCoverageImporter importer;

   public void run(IProgressMonitor progressMonitor, VectorCastAdaCoverageImporter importer) throws OseeCoreException {
      this.importer = importer;
      if (progressMonitor != null) {
         progressMonitor.beginTask("nVerifying aggregate.html report with Imported results", 1);
      }
      importer.getCoverageImport().getLog().log("\nVerifying aggregate.html report with Imported results");

      File reportHtmlFile = getFile();
      if (!reportHtmlFile.exists()) {
         throw new OseeArgumentException("VectorCast vcast_aggregate_coverage_report.html file doesn't exist [%s]",
            importer.getVcastDirectory());
      }
      try {
         Map<String, CoverageUnit> coverageNameToCoverageUnit = new HashMap<String, CoverageUnit>(5000);
         for (ICoverage unit : importer.getCoverageImport().getChildren(true)) {
            if (unit instanceof CoverageUnit) {
               coverageNameToCoverageUnit.put(unit.getName(), (CoverageUnit) unit);
            }
         }

         Reader inStream = new InputStreamReader(new FileInputStream(reportHtmlFile), "UTF-8");
         BufferedReader bufferedReader = new BufferedReader(inStream);
         String line;

         AggregateCoverageUnitResult result = null;
         while ((line = bufferedReader.readLine()) != null) {
            for (String subStr : line.split("<strong>")) {
               Matcher m = coverageUnitPattern.matcher(subStr);
               if (m.find()) {
                  if (result != null) {
                     throw new OseeStateException("Found coverage begin before last coverage end");
                  }
                  result = new AggregateCoverageUnitResult(m.group(1));
                  //               System.out.println("Found name " + m.group(1));
               }
               m = resultsPattern.matcher(subStr);
               if (m.find()) {
                  if (result == null) {
                     throw new OseeStateException("Found coverage end before begin");
                  }
                  result.setNumCovered(new Integer(m.group(1)));
                  result.setNumLines(new Integer(m.group(2)));
                  //               System.out.println("Found covered " + result.getNumCovered() + " of " + result.getNumLines());
                  verifyAggregateCoverageUnitResult(importer, coverageNameToCoverageUnit, result);
                  result = null;
               }
               if (subStr.contains(NO_COVERAGE_DATA_EXISTS)) {
                  if (result == null) {
                     throw new OseeStateException("Found \"No Coverage Data Exists\" before result begin");
                  }
                  result.setNotes("No Coverage Data Exists");
                  result = null;
               }
            }
         }
         bufferedReader.close();
         if (!error) {
            importer.getCoverageImport().getLog().log("Ok");
         }
         if (progressMonitor != null) {
            progressMonitor.worked(1);
         }

      } catch (Exception ex) {
         throw new OseeWrappedException("Error parsing aggregate report", ex);
      }
   }

   private void verifyAggregateCoverageUnitResult(VectorCastAdaCoverageImporter importer, Map<String, CoverageUnit> coverageNameToCoverageUnit, AggregateCoverageUnitResult result) {
      CoverageUnit coverageUnit = coverageNameToCoverageUnit.get(result.getName());
      if (coverageUnit == null) {
         importer.getCoverageImport().getLog().logError(
            String.format("Aggregate Check: Can't locate Coverage Unit for Aggregate unit [%s]", result.getName()));
         error = true;
      } else {
         // clear out of map to recover memory
         coverageNameToCoverageUnit.remove(result.getName());

         int importCuItems = coverageUnit.getCoverageItems(true).size();
         int importCuCovered = coverageUnit.getCoverageItemsCovered(true, CoverageOptionManager.Test_Unit).size();
         if (result.getNumLines() == null || result.getNumLines() != importCuItems || result.getNumCovered() == null || result.getNumCovered() != importCuCovered) {
            // Don't display error if this is the known ignore case
            if (!importer.isVectorCastIgnoreCase(result.getNotes(), importCuCovered, result.getNumCovered())) {
               importer.getCoverageImport().getLog().logError(
                  String.format(
                     "Aggregate Check: Unit [%s] Import [%d] of [%d] doesn't match Aggregate [%d] of [%d] [%s]",
                     result.getName(), importCuCovered, importCuItems, result.getNumCovered(), result.getNumLines(),
                     Strings.isValid(result.getNotes()) ? " - " + result.getNotes() : ""));
               error = true;
            }
         }
      }
   }

   public File getFile() {
      return new File(importer.getVcastDirectory() + "/vcast/vcast_aggregate_coverage_report.html");
   }

}
