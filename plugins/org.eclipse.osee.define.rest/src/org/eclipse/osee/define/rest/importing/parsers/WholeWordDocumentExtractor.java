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
package org.eclipse.osee.define.rest.importing.parsers;

import java.io.File;
import java.io.FileFilter;
import java.net.URI;
import org.eclipse.define.api.importing.RoughArtifact;
import org.eclipse.define.api.importing.RoughArtifactCollector;
import org.eclipse.define.api.importing.RoughArtifactKind;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.OrcsApi;

public class WholeWordDocumentExtractor extends AbstractArtifactExtractor {
   @Override
   public String getDescription() {
      return "Extract all the content of each Word XML document as one artifact.";
   }

   @Override
   protected XResultData extractFromSource(OrcsApi orcsApi, XResultData results, URI source, RoughArtifactCollector collector) throws Exception {
      if (source == null) {
         throw new OseeArgumentException("importFile can not be null");
      }
      RoughArtifact roughArtifact = new RoughArtifact(orcsApi, results, RoughArtifactKind.PRIMARY,
         Lib.removeExtension(new File(source).getName()));
      collector.addRoughArtifact(roughArtifact);
      roughArtifact.addAttribute(CoreAttributeTypes.WholeWordContent, source);
      return results;
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

   @Override
   public boolean usesTypeList() {
      return true;
   }

}