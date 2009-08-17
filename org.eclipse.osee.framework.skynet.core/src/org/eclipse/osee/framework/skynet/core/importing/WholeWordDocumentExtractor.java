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
package org.eclipse.osee.framework.skynet.core.importing;

import java.io.File;
import java.io.FileFilter;
import java.net.URI;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;

public class WholeWordDocumentExtractor extends WordExtractor {
   public String getDescription() {
      return "Extract all the content of each Word XML document as one artifact";
   }

   public void process(URI source, Branch branch) throws Exception {
      if (source == null) {
         throw new IllegalArgumentException("importFile can not be null");
      }
      RoughArtifact roughArtifact =
            new RoughArtifact(RoughArtifactKind.PRIMARY, branch, Lib.removeExtension(new File(source).getName()));
      addRoughArtifact(roughArtifact);
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