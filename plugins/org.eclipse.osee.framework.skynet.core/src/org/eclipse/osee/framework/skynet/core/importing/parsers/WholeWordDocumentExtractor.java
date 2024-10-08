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

import java.io.File;
import java.io.FileFilter;
import java.net.URI;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughArtifactCollector;

public class WholeWordDocumentExtractor extends AbstractArtifactExtractor {
   @Override
   public String getDescription() {
      return "Extract all the content of each Word XML document as one artifact.";
   }

   @Override
   protected void extractFromSource(OperationLogger logger, URI source, RoughArtifactCollector collector)
      throws Exception {
      if (source == null) {
         throw new OseeArgumentException("importFile can not be null");
      }
      RoughArtifact roughArtifact =
         new RoughArtifact(CoreArtifactTypes.MsWordWholeDocument, Lib.removeExtension(new File(source).getName()));
      collector.addRoughArtifact(roughArtifact);
      roughArtifact.addAttribute(CoreAttributeTypes.WholeWordContent, source);
   }

   @Override
   public FileFilter getFileFilter() {
      return new FileFilter() {
         @Override
         public boolean accept(File file) {
            return file.isDirectory() || file.isFile() && file.getName().endsWith(".xml");
         }
      };
   }

   @Override
   public String getName() {
      return "Whole Word Document";
   }
}