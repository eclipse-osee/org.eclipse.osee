/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.skynet.core.importing.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.InputStreamReader;
import java.net.URI;
import org.eclipse.osee.framework.core.data.MicrosoftOfficeApplicationEnum;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughArtifactCollector;

public class NativeDocumentExtractor extends AbstractArtifactExtractor {

   @Override
   public String getDescription() {
      return "Extract the content of each native document as one artifact.";
   }

   @Override
   protected void extractFromSource(OperationLogger logger, URI source, RoughArtifactCollector collector) {
      String extension = Lib.getExtension(source.toASCIIString());
      String name = Lib.removeExtension(new File(source).getName());

      RoughArtifact roughArtifact = new RoughArtifact(CoreArtifactTypes.NativeArtifact, name);
      collector.addRoughArtifact(roughArtifact);
      roughArtifact.addAttribute(CoreAttributeTypes.Extension, extension);
      roughArtifact.addAttribute(CoreAttributeTypes.NativeContent, source);
      try {
         MicrosoftOfficeApplicationEnum msoApplicationName = MsoApplicationExtractor.findMsoApplicationValue(
            new BufferedReader(new InputStreamReader(source.toURL().openStream())));
         roughArtifact.addAttribute(CoreAttributeTypes.MicrosoftOfficeApplication,
            msoApplicationName.getApplicationName());
      } catch (Exception ex) {
         OseeLog.log(NativeDocumentExtractor.class, OseeLevel.SEVERE_POPUP, ex);
      }

   }

   @Override
   public FileFilter getFileFilter() {
      return new FileFilter() {
         @Override
         public boolean accept(File file) {
            return true;
         }
      };
   }

   @Override
   public String getName() {
      return "General Documents (Any Format)";
   }
}