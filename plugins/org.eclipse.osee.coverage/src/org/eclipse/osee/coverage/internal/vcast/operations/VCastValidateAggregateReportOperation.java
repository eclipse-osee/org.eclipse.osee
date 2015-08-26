/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.internal.vcast.operations;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoverageOptionManager;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.vcast.model.AggregateCoverageUnitResult;

/**
 * @author Donald G. Dunne
 */
public class VCastValidateAggregateReportOperation extends AbstractOperation {

   /**
    * For VectorCast Version 5.3: String NO_COVERAGE_DATA_EXISTS = "No&nbsp;Coverage&nbsp;Data&nbsp;Available"; For
    * VectorCast Version 5.0:
    */
   private static final String NO_COVERAGE_DATA_EXISTS_PATTERN = "No&nbsp;Coverage&nbsp;Data&nbsp;Exists";

   private static final Pattern COVERAGE_UNIT_PATTERN =
      Pattern.compile("Code&nbsp;Coverage&nbsp;for&nbsp;Unit:&nbsp;(.*?)<");
   private static final Pattern RESULTS_PATTERN =
      Pattern.compile("&nbsp;([0-9]+)&nbsp;of&nbsp;([0-9]+)&nbsp;Lines&nbsp;Covered&nbsp;");

   private final XResultData logger;
   private final CoverageImport coverageImport;
   private final URI reportUri;
   private final Matcher coverageUnitMatcher;
   private final Matcher resultsMatcher;

   public VCastValidateAggregateReportOperation(XResultData logger, CoverageImport coverageImport, URI reportUri) {
      super("Verify aggregate report", Activator.PLUGIN_ID);
      this.logger = logger;
      this.reportUri = reportUri;
      this.coverageImport = coverageImport;

      this.coverageUnitMatcher = COVERAGE_UNIT_PATTERN.matcher("");
      this.resultsMatcher = RESULTS_PATTERN.matcher("");
   }

   private String getNoCoveragePattern() {
      String toReturn = NO_COVERAGE_DATA_EXISTS_PATTERN;
      String usevectorcast53 = System.getProperty("usevectorcast53", null);
      if (Strings.isValid(usevectorcast53)) {
         toReturn = "No&nbsp;Coverage&nbsp;Data&nbsp;Available";
      }
      return toReturn;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      monitor.beginTask("Verifying aggregate.html report with Imported results", 1);

      logger.log("\nVerifying aggregate.html report with Imported results");

      Map<String, CoverageUnit> coverageNameToCoverageUnit = getCoverageNameToUnits();
      String noCoveragePattern = getNoCoveragePattern();

      boolean error = false;
      AggregateCoverageUnitResult result = null;

      BufferedReader bufferedReader = null;
      try {
         bufferedReader = new BufferedReader(new InputStreamReader(reportUri.toURL().openStream(), "UTF-8"));
         String line;
         while ((line = bufferedReader.readLine()) != null) {
            checkForCancelledStatus(monitor);
            for (String subStr : line.split("<strong>")) {
               checkForCancelledStatus(monitor);

               coverageUnitMatcher.reset(subStr);
               resultsMatcher.reset(subStr);
               if (coverageUnitMatcher.find()) {
                  if (result != null) {
                     throw new OseeStateException(
                        "Found coverage begin before last coverage end. Perhaps you forgot to define the uservectorcast53 system variable?");
                  }
                  result = new AggregateCoverageUnitResult(coverageUnitMatcher.group(1));
               }

               if (resultsMatcher.find()) {
                  if (result == null) {
                     throw new OseeStateException("Found coverage end before begin");
                  }
                  error = parseResults(logger, coverageNameToCoverageUnit, result);
                  result = null;
               }

               if (subStr.contains(noCoveragePattern)) {
                  if (result == null) {
                     throw new OseeStateException("Found \"No Coverage Data Exists\" before result begin");
                  }
                  result.setNotes("No Coverage Data Exists");
                  result = null;
               }
            }
         }
      } finally {
         Lib.close(bufferedReader);
      }

      monitor.worked(1);

      if (!error) {
         logger.log("Ok");
      }
   }

   private Map<String, CoverageUnit> getCoverageNameToUnits() {
      Map<String, CoverageUnit> coverageNameToCoverageUnit = new HashMap<String, CoverageUnit>(5000);

      Collection<? extends ICoverage> units = coverageImport.getChildren(true);
      for (ICoverage unit : units) {
         if (unit instanceof CoverageUnit) {
            coverageNameToCoverageUnit.put(unit.getName(), (CoverageUnit) unit);
         }
      }
      return coverageNameToCoverageUnit;
   }

   private boolean parseResults(XResultData logger, Map<String, CoverageUnit> coverageNameToCoverageUnit, AggregateCoverageUnitResult result) {
      boolean toReturn = false;
      result.setNumCovered(new Integer(resultsMatcher.group(1)));
      result.setNumLines(new Integer(resultsMatcher.group(2)));

      CoverageUnit coverageUnit = coverageNameToCoverageUnit.get(result.getName());
      if (coverageUnit == null) {
         logger.error(String.format("Aggregate Check: Can't locate Coverage Unit for Aggregate unit [%s]",
            result.getName()));
         toReturn = true;
      } else {
         // clear out of map to recover memory
         coverageNameToCoverageUnit.remove(result.getName());

         int importCuItems = coverageUnit.getCoverageItems(true).size();
         int importCuCovered = coverageUnit.getCoverageItemsCount(true, CoverageOptionManager.Test_Unit);
         if (result.getNumLines() == null || result.getNumLines() != importCuItems || result.getNumCovered() == null || result.getNumCovered() != importCuCovered) {
            // Don't display error if this is the known ignore case
            if (!isVectorCastIgnoreCase(result.getNotes(), importCuCovered, result.getNumCovered())) {
               logger.error(String.format(
                  "Aggregate Check: Unit [%s] Import [%d] of [%d] doesn't match Aggregate [%d] of [%d] [%s]",
                  result.getName(), importCuCovered, importCuItems, result.getNumCovered(), result.getNumLines(),
                  Strings.isValid(result.getNotes()) ? " - " + result.getNotes() : ""));
               toReturn = true;
            }
         }
      }
      return toReturn;
   }

   /**
    * VectorCast does not put break-out information for coverage units that have no coverage. Check for this case so we
    * don't show lots of errors.
    */
   public boolean isVectorCastIgnoreCase(String notes, Integer importCuCovered, Integer aggregateNumCovered) {
      return notes.equals("No Coverage Data Exists") && importCuCovered == 0 && aggregateNumCovered == null;
   }

}
