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
import java.sql.SQLException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;

public class WholeWordDocumentExtractor extends WordExtractor {
   private static final String description = "Extract all the content of each Word XML document as one artifact";
   private final ArtifactSubtypeDescriptor primaryDescriptor;

   public WholeWordDocumentExtractor(ArtifactSubtypeDescriptor primaryDescriptor, Branch branch) throws SQLException {
      super(branch);
      this.primaryDescriptor = primaryDescriptor;
   }

   public static String getDescription() {
      return description;
   }

   /* (non-Javadoc)
    * @see osee.define.artifact.Import.ArtifactExtractor#discoverArtifactAndRelationData(java.io.File)
    */
   public void discoverArtifactAndRelationData(File importFile) throws Exception {
      if (importFile == null) throw new IllegalArgumentException("importFile can not be null");

      RoughArtifact roughArtifact = new RoughArtifact(Lib.stripExtension(importFile.getName()));
      roughArtifact.setPrimaryDescriptor(primaryDescriptor);
      addRoughArtifact(roughArtifact);

      String contents = Lib.fileToString(importFile);
      int beginIndex = contents.indexOf(BODY_START);
      int endIndex = contents.indexOf(BODY_END);

      if (beginIndex == -1 || endIndex == -1 || endIndex < beginIndex) {
         throw new IllegalArgumentException("The file " + importFile.getName() + " is not the right format");
      }
      beginIndex += BODY_START.length();
      contents = contents.substring(beginIndex, endIndex);

      roughArtifact.addAttribute(WordAttribute.CONTENT_NAME, contents);
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
}