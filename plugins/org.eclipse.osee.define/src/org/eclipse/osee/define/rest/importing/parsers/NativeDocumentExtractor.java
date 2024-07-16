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

package org.eclipse.osee.define.rest.importing.parsers;

import java.io.File;
import java.io.FileFilter;
import java.net.URI;
import org.eclipse.osee.define.rest.api.importing.RoughArtifact;
import org.eclipse.osee.define.rest.api.importing.RoughArtifactCollector;
import org.eclipse.osee.define.rest.api.importing.RoughArtifactKind;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.OrcsApi;

public class NativeDocumentExtractor extends AbstractArtifactExtractor {

   @Override
   public String getDescription() {
      return "Extract the content of each native document as one artifact.";
   }

   @Override
   protected XResultData extractFromSource(OrcsApi orcsApi, XResultData results, URI source, RoughArtifactCollector collector) {
      String extension = Lib.getExtension(source.toASCIIString());
      String name = Lib.removeExtension(new File(source).getName());

      RoughArtifact roughArtifact = new RoughArtifact(orcsApi, results, RoughArtifactKind.PRIMARY, name);
      collector.addRoughArtifact(roughArtifact);
      roughArtifact.addAttribute(CoreAttributeTypes.Extension, extension);
      roughArtifact.addAttribute(CoreAttributeTypes.NativeContent, source);
      return results;
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