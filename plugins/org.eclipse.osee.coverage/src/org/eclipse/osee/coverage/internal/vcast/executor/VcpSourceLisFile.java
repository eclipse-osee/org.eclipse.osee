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
package org.eclipse.osee.coverage.internal.vcast.executor;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.coverage.internal.vcast.operations.VcpSourceFile;
import org.eclipse.osee.coverage.model.ICoverageUnitFileContentsLoader;
import org.eclipse.osee.coverage.util.LineData;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * Reads <filename>.LIS file associated with a source file
 * 
 * @author Donald G. Dunne
 */
public class VcpSourceLisFile implements ICoverageUnitFileContentsLoader {

   private String[] lines = null;
   private String text = null;
   private final VcpSourceFile vcpSourceFile;

   public VcpSourceLisFile(VcpSourceFile vcpSourceFile) {
      this.vcpSourceFile = vcpSourceFile;
   }

   private void ensureLoaded() throws OseeCoreException {
      try {
         if (text == null) {
            String lisFilename = getLisFilename();
            if (lisFilename != null) {
               File listFile = new File(lisFilename);
               if (!listFile.exists()) {
                  throw new OseeArgumentException(String.format("VectorCast <filename>.LIS file doesn't exist [%s]",
                     lisFilename));
               }
               text = Lib.fileToString(listFile);
               lines = text.split("\n");
            }
         }
      } catch (IOException ex) {
         throw new OseeWrappedException(ex);
      }
   }

   private String getLisFilename() {
      String filename = vcpSourceFile.getFilename();
      if (filename != null) {
         return vcpSourceFile.getvCastVcp().getVCastDirectory() + File.separator + "vcast" + File.separator + filename.replaceFirst(
            "(.*)\\..*", "$1") + ".LIS";
      }
      return null;
   }

   @Override
   public String getText() throws OseeCoreException {
      ensureLoaded();
      return text;
   }

   private static final Pattern exceptionPattern = Pattern.compile("^\\s+EXCEPTION\\s*$");
   private static final Pattern endMethodPattern = Pattern.compile("^\\s*END\\s+(.*);\\s*$");

   public LineData getExecutionLine(String method, String executionLine) throws OseeCoreException {
      ensureLoaded();
      String startsWith = method + " " + executionLine + " ";
      boolean exceptionLine = false;
      int lineNum = 0;
      for (String line : lines) {
         lineNum++;
         if (line.startsWith(startsWith)) {
            return new LineData(line, exceptionLine, lineNum);
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

   public File getFile() {
      return new File(getLisFilename());
   }

   @Override
   public String toString() {
      try {
         return getLisFilename();
      } catch (Exception ex) {
         // do nothing
      }
      return super.toString();
   }

}
