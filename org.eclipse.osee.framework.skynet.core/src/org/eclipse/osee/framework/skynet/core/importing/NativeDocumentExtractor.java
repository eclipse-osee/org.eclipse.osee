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
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.CoreAttributes;

public class NativeDocumentExtractor extends AbstractArtifactExtractor {

   public String getDescription() {
      return "Extract the content of each native document as one artifact";
   }

   public void discoverArtifactAndRelationData(File importFile, Branch branch) throws Exception {
      RoughArtifact roughArtifact =
            new RoughArtifact(RoughArtifactKind.PRIMARY, branch, Lib.removeExtension(importFile.getName()));
      addRoughArtifact(roughArtifact);
      roughArtifact.addAttribute(CoreAttributes.NATIVE_EXTENSION.getName(), Lib.getExtension(importFile.getName()));
      roughArtifact.addFileAttribute(CoreAttributes.NATIVE_CONTENT.getName(), importFile);
   }

   public FileFilter getFileFilter() {
      return new FileFilter() {
         public boolean accept(File file) {
            return true;
         }
      };
   }

   @Override
   public String getName() {
      return "General Documents (Any Format)";
   }

   @Override
   public boolean usesTypeList() {
      return true;
   }
}