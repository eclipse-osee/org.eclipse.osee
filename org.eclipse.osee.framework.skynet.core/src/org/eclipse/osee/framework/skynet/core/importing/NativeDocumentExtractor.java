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
import org.eclipse.osee.framework.skynet.core.attribute.CoreAttributes;

public class NativeDocumentExtractor extends AbstractArtifactExtractor {

   public String getDescription() {
      return "Extract the content of each native document as one artifact";
   }

   public void process(URI source, Branch branch) throws Exception {
      String extension = Lib.getExtension(source.toASCIIString());
      String name = Lib.removeExtension(new File(source).getName());

      RoughArtifact roughArtifact = new RoughArtifact(RoughArtifactKind.PRIMARY, branch, name);
      addRoughArtifact(roughArtifact);
      roughArtifact.addAttribute(CoreAttributes.NATIVE_EXTENSION.getName(), extension);
      roughArtifact.addURIAttribute(CoreAttributes.NATIVE_CONTENT.getName(), source);
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