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
package org.eclipse.osee.framework.skynet.core.importing.parsers;

import java.io.File;
import java.io.FileFilter;
import java.net.URI;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifactKind;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughArtifactCollector;

public class WholeWordDocumentExtractor extends AbstractArtifactExtractor {
   public String getDescription() {
      return "Extract all the content of each Word XML document as one artifact.";
   }

   @Override
   protected void extractFromSource(URI source, RoughArtifactCollector collector) throws Exception {
      if (source == null) {
         throw new OseeArgumentException("importFile can not be null");
      }
      RoughArtifact roughArtifact =
            new RoughArtifact(RoughArtifactKind.PRIMARY, Lib.removeExtension(new File(source).getName()));
      collector.addRoughArtifact(roughArtifact);
      roughArtifact.addURIAttribute(WordAttribute.WHOLE_WORD_CONTENT, source);
   }

   public FileFilter getFileFilter() {
      return new FileFilter() {
         public boolean accept(File file) {
            return file.isDirectory() || file.isFile() && file.getName().endsWith(".xml");
         }
      };
   }

   @Override
   public String getName() {
      return "Whole Word Document";
   }

   @Override
   public boolean usesTypeList() {
      return true;
   }
}