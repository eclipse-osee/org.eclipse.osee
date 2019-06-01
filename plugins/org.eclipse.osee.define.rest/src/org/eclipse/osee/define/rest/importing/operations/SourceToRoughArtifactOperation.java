/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.rest.importing.operations;

import java.io.File;
import org.eclipse.define.api.importing.IArtifactExtractor;
import org.eclipse.define.api.importing.RoughArtifact;
import org.eclipse.define.api.importing.RoughArtifactCollector;
import org.eclipse.define.api.importing.RoughArtifactKind;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Roberto E. Escobar
 * @author David W. Miller
 */
public class SourceToRoughArtifactOperation {

   private final OrcsApi orcsApi;
   private final IArtifactExtractor extractor;
   private final File sourceFile;
   private final RoughArtifactCollector collector;
   private final XResultData results;

   public SourceToRoughArtifactOperation(OrcsApi orcsApi, XResultData results, IArtifactExtractor extractor, File sourceFile, RoughArtifactCollector collector) {
      this.extractor = extractor;
      this.sourceFile = sourceFile;
      this.collector = collector;
      this.orcsApi = orcsApi;
      this.results = results;
   }

   public XResultData importFiles() {
      File[] files;
      files = new File[] {sourceFile};
      results.combine(extractArtifacts(files, collector, collector.getParentRoughArtifact()));
      return results;
   }

   /**
    * used recursively when originally passed a directory, thus an array of files is accepted
    */
   private XResultData extractArtifacts(File[] files, RoughArtifactCollector collector, RoughArtifact parentArtifact) {
      for (File file : files) {
         if (file.isFile()) {
            results.combine(processFile(file, collector, parentArtifact));
         } else if (file.isDirectory()) {
            RoughArtifact directoryArtifact =
               new RoughArtifact(orcsApi, results, RoughArtifactKind.CONTAINER, file.getName());
            collector.addChildRoughArtifact(directoryArtifact);
            File[] subFiles = file.listFiles(extractor.getFileFilter());
            if (files.length > 0) {
               results.combine(extractArtifacts(subFiles, collector, directoryArtifact));
            }
         } else {
            throw new OseeStateException("Source location [%s] is not a valid file or directory", file);
         }
      }
      return results;
   }

   private XResultData processFile(File file, RoughArtifactCollector collector, RoughArtifact parent) {
      XResultData results = new XResultData();
      RoughArtifactCollector tempCollector = new RoughArtifactCollector(parent);
      try {
         results.combine(extractor.process(orcsApi, results, file.toURI(), tempCollector));
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
      // pass through all the collected items
      collector.addAllRoughArtifacts(tempCollector.getRoughArtifacts());
      collector.addAllRoughRelations(tempCollector.getRoughRelations());
      return results;
   }
}
