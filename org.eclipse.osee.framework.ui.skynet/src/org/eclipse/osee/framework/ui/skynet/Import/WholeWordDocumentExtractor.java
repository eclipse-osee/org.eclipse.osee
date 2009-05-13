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
package org.eclipse.osee.framework.ui.skynet.Import;

import java.io.File;
import java.io.FileFilter;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;

public class WholeWordDocumentExtractor extends WordExtractor {
   public String getDescription() {
      return "Extract all the content of each Word XML document as one artifact";
   }

   /* (non-Javadoc)
    * @see osee.define.artifact.Import.ArtifactExtractor#discoverArtifactAndRelationData(java.io.File)
    */
   public void discoverArtifactAndRelationData(File importFile, Branch branch) throws Exception {
      if (importFile == null) throw new IllegalArgumentException("importFile can not be null");
      RoughArtifact roughArtifact =
            new RoughArtifact(RoughArtifactKind.PRIMARY, branch, Lib.removeExtension(importFile.getName()));
      addRoughArtifact(roughArtifact);
      roughArtifact.addFileAttribute(WordAttribute.WHOLE_WORD_CONTENT, importFile);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.Import.ArtifactExtractor#getFileFilter()
    */
   public FileFilter getFileFilter() {
      return new FileFilter() {
         public boolean accept(File file) {
            return file.isDirectory() || (file.isFile() && file.getName().endsWith(".xml"));
         }
      };
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.Import.ArtifactExtractor#getName()
    */
   @Override
   public String getName() {
      return "Whole Word Document";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.Import.ArtifactExtractor#usesTypeList()
    */
   @Override
   public boolean usesTypeList() {
      return true;
   }
}