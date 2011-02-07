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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.vcast.CoverageDataUnit.CoverageDataType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Represents the vcast/<code file>.xml file.  Parses the xml within this file and generate
 * a CoverageDataUnit for each <coverage_data></coverage_data> block in file.  There is ususally
 * only one <coverage_data> and one <unit> per .xml file.
 * 
 * @author Donald G. Dunne
 */
public class CoverageDataFile {

   public List<CoverageDataUnit> coverageDataUnits = new ArrayList<CoverageDataUnit>();
   private static final Pattern lineNumToBranchesPattern = Pattern.compile("\\{([0-9]+);\\s*([0-9]+)\\}");
   private final String coverageDataFilename;

   public CoverageDataFile(CoverageImport coverageImport, String coverageDataFilename) throws OseeCoreException {

      this.coverageDataFilename = coverageDataFilename;
      File coverageDataFile = getFile();
      if (!coverageDataFile.exists()) {
         String errStr =
            String.format("VectorCast vcast/<code file>.xml file doesn't exist [%s]", coverageDataFilename);
         coverageImport.getLog().logError(AHTML.textToHtml(errStr));
         return;
      }

      try {
         String fileStr = Lib.fileToString(coverageDataFile);
         Document document = Jaxp.readXmlDocument(fileStr);
         List<Element> unitElements = Jaxp.findElements(document.getDocumentElement(), "unit");
         for (Element unitElement : unitElements) {
            CoverageDataUnit coverageDataUnit = new CoverageDataUnit(Jaxp.getChildText(unitElement, "name"));
            coverageDataUnit.setIndex(new Integer(unitElement.getAttribute("index")).intValue());
            coverageDataUnit.setCoverageType(CoverageDataType.valueOf(unitElement.getAttribute("coverage_type")));

            List<Element> subprograms = Jaxp.findElements(unitElement, "subprogram");
            int subprogramNum = 0;
            for (Element subprogram : subprograms) {
               subprogramNum++;
               String subprogramName = Jaxp.getChildText(subprogram, "name");
               if (!Strings.isValid(subprogramName)) {
                  String errStr =
                     String.format(
                        "Subprogram name not found for subprogram number %d in vcast/<code file>.xml for <code file> [%s]. Skipping",
                        subprogramNum, coverageDataFile);
                  coverageImport.getLog().logError(AHTML.textToHtml(errStr));
               } else {
                  CoverageDataSubProgram coverageDataSubProgram = new CoverageDataSubProgram(subprogramName);
                  String lineNumbersToBranches = Jaxp.getChildText(subprogram, "line_numbers_to_branches");
                  Matcher m = lineNumToBranchesPattern.matcher(lineNumbersToBranches);
                  while (m.find()) {
                     // Don't know what to do with >0 branches yet; assume branch coverage (future)
                     if (!m.group(2).equals("0")) {
                        String errStr =
                           String.format("Unhandled branches [%s] for lineNum [%s] subprogram [%s]", m.group(1),
                              m.group(1), subprogramName);
                        coverageImport.getLog().logError(errStr);
                     }
                     coverageDataSubProgram.addLineNumToBranches(new Integer(m.group(1)).intValue(),
                        new Integer(m.group(2)));
                  }

                  Element metricsElement = Jaxp.getChild(subprogram, "metrics");
                  String complexity = Jaxp.getChildText(metricsElement, "complexity");
                  Element coverageElement = Jaxp.getChild(metricsElement, "coverage");
                  if (!coverageElement.getAttribute("coverage_type").equals("STATEMENT")) {
                     String errStr =
                        String.format("Unhandled coverage_type [%s] for subprogram [%s].  Skipping",
                           coverageElement.getAttribute("coverage_type"), subprogramName);
                     coverageImport.getLog().logError(errStr);
                     continue;
                  }
                  String coveredElement = Jaxp.getChildText(coverageElement, "covered");
                  String totalElement = Jaxp.getChildText(coverageElement, "total");
                  coverageDataSubProgram.setComplexity(new Integer(complexity).intValue());
                  coverageDataSubProgram.setCovered(new Integer(coveredElement).intValue());
                  coverageDataSubProgram.setTotal(new Integer(totalElement).intValue());

                  coverageDataUnit.addSubProgram(coverageDataSubProgram);
               }
            }
            coverageDataUnits.add(coverageDataUnit);
         }

      } catch (IOException ex) {
         throw new OseeWrappedException(String.format("Exception reading file [%s]", coverageDataFilename), ex);
      } catch (SAXException ex) {
         throw new OseeWrappedException(String.format("Exception parsing file [%s]", coverageDataFilename), ex);
      } catch (ParserConfigurationException ex) {
         throw new OseeWrappedException(String.format("Exception parsing file [%s]", coverageDataFilename), ex);
      }
   }

   public File getFile() {
      return new File(coverageDataFilename);
   }

   public List<CoverageDataUnit> getCoverageDataUnits() {
      return coverageDataUnits;
   }
}
