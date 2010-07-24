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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;

/**
 * @author Donald G. Dunne
 */
public class VCastAggregateReport {

   private final String vcastDirectory;
   Pattern coverageUnitPattern = Pattern.compile("Code&nbsp;Coverage&nbsp;for&nbsp;Unit:&nbsp;(.*?)<");
   Pattern resultsPattern = Pattern.compile("&nbsp;([0-9]+)&nbsp;of&nbsp;([0-9]+)&nbsp;Lines&nbsp;Covered&nbsp;");
   String NO_COVERAGE_DATA_EXISTS = "No&nbsp;Coverage&nbsp;Data&nbsp;Exists";

   public VCastAggregateReport(String vcastDirectory) throws OseeCoreException {
      this.vcastDirectory = vcastDirectory;
   }

   public List<AggregateCoverageUnitResult> getResults() throws OseeCoreException {
      File reportHtmlFile = getFile();
      if (!reportHtmlFile.exists()) {
         throw new OseeArgumentException(String.format(
            "VectorCast vcast_aggregate_coverage_report.html file doesn't exist [%s]", vcastDirectory));
      }
      List<AggregateCoverageUnitResult> results = new ArrayList<AggregateCoverageUnitResult>();
      try {
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
                  results.add(result);
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
      } catch (Exception ex) {
         throw new OseeWrappedException("Error parsing aggregate report", ex);
      }
      return results;
   }

   public File getFile() {
      return new File(vcastDirectory + "/vcast/vcast_aggregate_coverage_report.html");
   }

}
