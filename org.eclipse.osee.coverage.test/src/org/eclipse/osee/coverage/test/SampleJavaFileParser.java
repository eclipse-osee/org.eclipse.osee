/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageMethodEnum;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Donald G. Dunne
 */
public class SampleJavaFileParser {

   private static final Pattern methodPatter =
         Pattern.compile("\\s+(public|private)\\s(\\w+)\\s(\\w+)\\(.*\\)\\s+\\{\\s*");
   private static final Pattern executeLine = Pattern.compile("^(.*)\\s+//\\s+(\\w+),\\s+(\\w+),\\s+(\\w+)");

   public static CoverageUnit createCodeUnit(URL url) throws OseeCoreException {
      try {
         if (url == null) {
            throw new IllegalArgumentException("Valid filename must be specified");
         }
         InputStream inputStream = url.openStream();
         if (inputStream == null) {
            throw new IllegalArgumentException(String.format("File doesn't exist [%s]", url));
         }
         // Store file as CoverageUnit
         File file = new File(url.getFile());
         String filename = file.getCanonicalFile().getName();
         CoverageUnit fileCoverageUnit = new CoverageUnit(null, filename, url.getFile());
         String fileStr = Lib.inputStreamToString(inputStream);
         fileCoverageUnit.setText(fileStr);
         CoverageUnit coverageUnit = null;
         int lineNum = 0;
         for (String line : fileStr.split("\r\n")) {
            lineNum++;
            // Determine if method; store as CoverageUnit
            Matcher m = methodPatter.matcher(line);
            if (m.find()) {
               String name = m.group(3);
               coverageUnit = new CoverageUnit(fileCoverageUnit, name, "Line " + lineNum);
               fileCoverageUnit.addCoverageUnit(coverageUnit);
            }
            // Determine if executable coverage line; store as CoverageItem
            m = executeLine.matcher(line);
            if (m.find()) {
               String lineText = m.group(1);
               String methodNum = m.group(2);
               String executeNum = m.group(3);
               boolean covered = m.group(4).equals("y");
               CoverageItem coverageItem =
                     new CoverageItem(coverageUnit, covered ? CoverageMethodEnum.Test_Unit : CoverageMethodEnum.None,
                           executeNum);
               coverageItem.setLineNum(String.valueOf(lineNum));
               coverageItem.setText(lineText);
               coverageItem.setMethodNum(methodNum);
               coverageUnit.addCoverageItem(coverageItem);
            }
         }
         return fileCoverageUnit;
      } catch (IOException ex) {
         throw new OseeWrappedException(ex);
      }
   }
}
