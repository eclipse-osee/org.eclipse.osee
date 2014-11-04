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
package org.eclipse.osee.coverage.internal.vcast.operations;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.vcast.model.VCastVcp;
import org.eclipse.osee.vcast.model.VcpResultsFile;
import org.eclipse.osee.vcast.model.VcpSourceFile;

/**
 * Represents the <dir>.wrk/vcast.vcp file which lists all the source files and results files specified in this
 * directory.
 * 
 * @author Donald G. Dunne
 */
public class VCastVcpLoadOperation extends AbstractOperation {

   private static final Pattern VALUE_PATTERN = Pattern.compile("(.*?):(.*?)$");

   private final URI vcpUri;
   private final VCastVcp vcastVcp;

   private final Matcher valueMatcher;

   public VCastVcpLoadOperation(URI vcpUri, VCastVcp vcastVcp) {
      super("Load VcastVcp", Activator.PLUGIN_ID);
      this.vcpUri = vcpUri;
      this.vcastVcp = vcastVcp;

      valueMatcher = VALUE_PATTERN.matcher("");
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      Conditions.checkNotNull(vcpUri, "vcpUri");
      Conditions.checkNotNull(vcastVcp, "vcastVcp");

      List<VcpResultsFile> resultsFiles = vcastVcp.getResultsFiles();
      List<VcpSourceFile> sourceFiles = vcastVcp.getSourceFiles();

      BufferedReader br = null;
      try {
         br = new BufferedReader(new InputStreamReader(vcpUri.toURL().openStream(), "UTF-8"));
         String line;
         VcpSourceFile vcpSourceFile = null;
         VcpResultsFile vcpResultsFile = null;
         // Loop through results file and log coverageItem as Test_Unit for each entry
         while ((line = br.readLine()) != null) {
            if (line.startsWith("SOURCE_FILE_BEGIN")) {
               vcpSourceFile = new VcpSourceFile(vcastVcp);
            } else if (line.startsWith("SOURCE_FILE_END")) {
               sourceFiles.add(vcpSourceFile);
               vcpSourceFile = null;
            } else if (vcpSourceFile != null) {
               addToSourceFile(vcpSourceFile, line);
            } else if (line.startsWith("RESULT_FILE_BEGIN")) {
               vcpResultsFile = new VcpResultsFile(vcastVcp);
            } else if (line.startsWith("RESULT_FILE_END")) {
               resultsFiles.add(vcpResultsFile);
               vcpResultsFile = null;
            } else if (vcpResultsFile != null) {
               addToResultFile(vcpResultsFile, line);
            }
         }
      } finally {
         Lib.close(br);
      }
   }

   private Pair<String, String> toKeyValue(String line) {
      Pair<String, String> entry = null;
      valueMatcher.reset(line);
      if (valueMatcher.find()) {
         String key = valueMatcher.group(1);
         String value = valueMatcher.group(2);
         entry = new Pair<String, String>(key, value);
      }
      return entry;
   }

   private void addToSourceFile(VcpSourceFile vcpSourceFile, String line) {
      Pair<String, String> entry = toKeyValue(line);
      if (entry != null) {
         String key = entry.getFirst();
         if ("SOURCE_FILENAME".equals(key)) {
            vcpSourceFile.setFilename(entry.getSecond());
         } else if ("UNIT_NUMBER".equals(key)) {
            vcpSourceFile.setUnitNumber(entry.getSecond());
         }
      } else {
         OseeLog.logf(Activator.class, Level.SEVERE, "Unhandled VcpSourceFile line [%s]", line);
      }
   }

   private void addToResultFile(VcpResultsFile vcpResultsFile, String line) {
      Pair<String, String> entry = toKeyValue(line);
      if (entry != null) {
         String key = entry.getFirst();
         if ("FILENAME".equals(key)) {
            vcpResultsFile.setFilename(entry.getSecond());
         }
      } else {
         OseeLog.logf(Activator.class, Level.SEVERE, "Unhandled VcpResultsFile line [%s]", line);
      }
   }
}
