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

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class VcpSourceFile {

   private final Map<SourceValue, String> sourceValues = new HashMap<SourceValue, String>(20);
   Pattern valuePattern = Pattern.compile("(.*?):(.*?)$");
   private VcpSourceLineFile vcpSourceLineFile = null;
   private VcpSourceLisFile vcpSourceLisFile = null;
   private CoverageDataFile coverageDataFile = null;
   private final String vcastDirectory;

   public static enum SourceValue {
      SOURCE_FILENAME,
      SOURCE_DIRECTORY,
      DEST_FILENAME,
      DEST_DIRECTORY,
      DISPLAY_NAME,
      UNIT_NUMBER,
      FILE_TYPE,
      IS_BACKUP,
      COVERAGE,
      COVERAGE_IO_TYPE,
      ADDITION_TIME,
      MODIFIED_TIME,
      CHECKSUM,
      UNINSTR_CHECKSUM
   };

   public VcpSourceFile(String vcastDirectory) {
      this.vcastDirectory = vcastDirectory;
   }

   public String getValue(SourceValue sourceValue) {
      return sourceValues.get(sourceValue);
   }

   public void addLine(String line) {
      Matcher m = valuePattern.matcher(line);
      if (m.find()) {
         SourceValue sourceValue = SourceValue.valueOf(m.group(1));
         if (sourceValue == null) {
            OseeLog.log(Activator.class, Level.SEVERE, String.format("Unhandled VcpSourceFile value [%s]", m.group(1)));
         } else {
            sourceValues.put(sourceValue, m.group(2));
         }
      } else {
         OseeLog.log(Activator.class, Level.SEVERE, String.format("Unhandled VcpSourceFile line [%s]", line));
      }
   }

   public VcpSourceLineFile getVcpSourceLineFile() throws OseeCoreException {
      if (vcpSourceLineFile == null) {
         vcpSourceLineFile = new VcpSourceLineFile(vcastDirectory, this);
      }
      return vcpSourceLineFile;
   }

   public VcpSourceLisFile getVcpSourceLisFile() throws OseeCoreException {
      if (vcpSourceLisFile == null) {
         vcpSourceLisFile = new VcpSourceLisFile(vcastDirectory, this);
      }
      return vcpSourceLisFile;
   }

   public CoverageDataFile getCoverageDataFile() throws OseeCoreException {
      if (coverageDataFile == null) {
         coverageDataFile =
               new CoverageDataFile(vcastDirectory + "/vcast/" + getValue(SourceValue.SOURCE_FILENAME).replaceAll(
                     "\\.(ada|adb)", "\\.xml"));
      }
      return coverageDataFile;
   }

   public String toString() {
      return getValue(SourceValue.SOURCE_FILENAME);
   }
}
