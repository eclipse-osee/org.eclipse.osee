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
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.coverage.vcast.VcpSourceFile.SourceValue;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.AFile;

/**
 * Reads <filename>.LIS file associated with a source file
 * 
 * @author Donald G. Dunne
 */
public class VcpSourceLisFile {

   File listFile = null;
   String[] lines = null;
   String text = null;
   String lisFilename = null;

   public VcpSourceLisFile(String vcastDirectory, VcpSourceFile vcpSourceFile) throws OseeCoreException {
      lisFilename =
            vcastDirectory + "/vcast/" + vcpSourceFile.getValue(SourceValue.SOURCE_FILENAME).replaceFirst("(.*)\\..*",
                  "$1") + ".LIS";
      listFile = getFile();
      if (!listFile.exists()) {
         throw new OseeArgumentException(
               String.format("VectorCast <filename>.LIS file doesn't exist [%s]", lisFilename));
      }
      text = AFile.readFile(listFile);
      lines = text.split("\n");
   }

   public File getFile() {
      return new File(lisFilename);
   }

   public String[] getSection(String startLine, String endLine) {
      return Arrays.copyOfRange(lines, new Integer(startLine), new Integer(endLine));
   }

   public String[] get() {
      return lines;
   }

   public String getText() {
      return text;
   }

   public String getPackage() {
      return "unknown - tbd";
   }

   private static Pattern exceptionPattern = Pattern.compile("^\\s+EXCEPTION\\s*$");
   private static Pattern endMethodPattern = Pattern.compile("^\\s*END\\s+(.*);\\s*$");

   public Pair<String, Boolean> getExecutionLine(String method, String executionLine) {
      String startsWith = method + " " + executionLine + " ";
      boolean exceptionLine = false;
      for (String line : lines) {
         if (line.startsWith(startsWith)) {
            return new Pair<String, Boolean>(line, exceptionLine);
         }
         Matcher m = exceptionPattern.matcher(line);
         if (m.find()) {
            exceptionLine = true;
         } else {
            m = endMethodPattern.matcher(line);
            if (m.find()) {
               exceptionLine = false;
            }
         }
      }
      return null;
   }

   @Override
   public String toString() {
      try {
         return listFile.getCanonicalPath();
      } catch (Exception ex) {
         // do nothing
      }
      return super.toString();
   }
}
