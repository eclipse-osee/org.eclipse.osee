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
import java.util.Objects;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.osee.define.api.importing.RoughArtifact;
import org.eclipse.osee.define.api.importing.RoughArtifactCollector;
import org.eclipse.osee.define.api.importing.RoughArtifactKind;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.OrcsApi;

public class WholeWordDocumentExtractor extends AbstractArtifactExtractor {
   @Override
   public String getDescription() {
      return "Extract all the content of each Word XML document as one artifact.";
   }

   @Override
   protected @NonNull XResultData extractFromSource(OrcsApi orcsApi, @NonNull XResultData results, @NonNull URI source,
      RoughArtifactCollector collector) throws Exception {

      Objects.requireNonNull(results,
         "WholeWordDocumentExtractor::extractFromSource, parameter \"results\" cannot be null.");

      Objects.requireNonNull(source,
         "WholeWordDocumentExtractor::extractFromSource, parameter \"source\" cannot be null.");

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
}