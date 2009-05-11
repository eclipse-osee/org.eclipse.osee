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
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.NativeArtifact;

public class NativeDocumentExtractor extends AbstractArtifactExtractor {
   private final ArtifactType folderDescriptor;

   public NativeDocumentExtractor() throws OseeCoreException {
      folderDescriptor = ArtifactTypeManager.getType("Folder");
   }

   public String getDescription() {
      return "Extract the content of each native document as one artifact";
   }

   /* (non-Javadoc)
    * @see osee.define.artifact.Import.ArtifactExtractor#discoverArtifactAndRelationData(java.io.File)
    */
   public void discoverArtifactAndRelationData(File importFile, Branch branch, ArtifactType primaryArtifactType) throws Exception {
      RoughArtifact roughArtifact = new RoughArtifact(branch, Lib.removeExtension(importFile.getName()));
      roughArtifact.setHeadingDescriptor(folderDescriptor);
      roughArtifact.setPrimaryArtifactType(primaryArtifactType);
      addRoughArtifact(roughArtifact);
      roughArtifact.addAttribute("Extension", Lib.getExtension(importFile.getName()));
      roughArtifact.addFileAttribute(NativeArtifact.CONTENT_NAME, importFile);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.Import.ArtifactExtractor#getFileFilter()
    */
   public FileFilter getFileFilter() {
      return new FileFilter() {
         public boolean accept(File file) {
            return true;
         }
      };
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.Import.ArtifactExtractor#getName()
    */
   @Override
   public String getName() {
      return "General Documents (Any Format)";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.Import.ArtifactExtractor#usesTypeList()
    */
   @Override
   public boolean usesTypeList() {
      return true;
   }
}