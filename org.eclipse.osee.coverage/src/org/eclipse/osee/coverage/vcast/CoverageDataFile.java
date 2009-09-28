/*
 * Created on Sep 26, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.vcast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.coverage.vcast.CoverageDataUnit.CoverageDataType;
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

   public CoverageDataFile(String vcastDirectory) {

      File coverageDataFile = new File(vcastDirectory + "/vcast/coverage_data.xml");
      if (!coverageDataFile.exists()) {
         throw new IllegalArgumentException(String.format("VectorCast coverage_data.xml file doesn't exist [%s]",
               vcastDirectory));
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

   public List<CoverageDataUnit> getCoverageDataUnits() {
      return coverageDataUnits;
   }
}
