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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.coverage.vcast.CoverageDataUnit.CoverageDataType;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AFile;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Donald G. Dunne
 */
public class CoverageDataFile {

   public List<CoverageDataUnit> coverageDataUnits = new ArrayList<CoverageDataUnit>();
   private static final Pattern lineNumToBranchesPattern = Pattern.compile("\\{([0-9]+);\\s*([0-9]+)\\}");
   private final String coverageDataFilename;

   public CoverageDataFile(String coverageDataFilename) throws OseeCoreException {

      this.coverageDataFilename = coverageDataFilename;
      File coverageDataFile = getFile();
      if (!coverageDataFile.exists()) {
         throw new OseeArgumentException(String.format("VectorCast coverage data file doesn't exist [%s]",
               coverageDataFilename));
      }
      String fileStr = AFile.readFile(coverageDataFile);
      try {
         Document document = Jaxp.readXmlDocument(fileStr);
         List<Element> unitElements = Jaxp.findElements(document.getDocumentElement(), "unit");
         for (Element unitElement : unitElements) {
            CoverageDataUnit coverageDataUnit = new CoverageDataUnit(Jaxp.getChildText(unitElement, "name"));
            coverageDataUnit.setIndex(new Integer(unitElement.getAttribute("index")).intValue());
            coverageDataUnit.setCoverageType(CoverageDataType.valueOf(unitElement.getAttribute("coverage_type")));

            List<Element> subprograms = Jaxp.findElements(unitElement, "subprogram");
            for (Element subprogram : subprograms) {
               String subprogramName = Jaxp.getChildText(subprogram, "name");
               CoverageDataSubProgram coverageDataSubProgram = new CoverageDataSubProgram(subprogramName);
               String lineNumbersToBranches = Jaxp.getChildText(subprogram, "line_numbers_to_branches");
               Matcher m = lineNumToBranchesPattern.matcher(lineNumbersToBranches);
               while (m.find()) {
                  // Don't know what to do with >0 branches yet; assume branch coverage (future)
                  if (!m.group(2).equals("0")) {
                     System.out.println(String.format("Unhandled branches [%s] for lineNum [%s] subprogram [%s]",
                           m.group(1), m.group(1), subprogramName));
                  }
                  coverageDataSubProgram.addLineNumToBranches(new Integer(m.group(1)).intValue(), new Integer(
                        m.group(2)));
               }

               Element metricsElement = Jaxp.getChild(subprogram, "metrics");
               String complexity = Jaxp.getChildText(metricsElement, "complexity");
               Element coverageElement = Jaxp.getChild(metricsElement, "coverage");
               if (!coverageElement.getAttribute("coverage_type").equals("STATEMENT")) {
                  System.out.println(String.format("Unhandled coverage_type [%s] for subprogram [%s].  Skipping",
                        coverageElement.getAttribute("coverage_type"), subprogramName));
                  continue;
               }
               String coveredElement = Jaxp.getChildText(coverageElement, "covered");
               String totalElement = Jaxp.getChildText(coverageElement, "total");
               coverageDataSubProgram.setComplexity(new Integer(complexity).intValue());
               coverageDataSubProgram.setCovered(new Integer(coveredElement).intValue());
               coverageDataSubProgram.setTotal(new Integer(totalElement).intValue());
               coverageDataUnit.addSubProgram(coverageDataSubProgram);
            }
            coverageDataUnits.add(coverageDataUnit);
         }

      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public File getFile() {
      return new File(coverageDataFilename);
   }

   public List<CoverageDataUnit> getCoverageDataUnits() {
      return coverageDataUnits;
   }
}
