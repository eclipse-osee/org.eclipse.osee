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
package org.eclipse.osee.framework.skynet.core.importing.operations;

import java.io.File;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifactKind;
import org.eclipse.osee.framework.skynet.core.importing.parsers.IArtifactExtractor;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class SourceToRoughArtifactOperation extends AbstractOperation {

   private final OperationLogger logger;
   private final IArtifactExtractor extractor;
   private final File sourceFile;
   private final RoughArtifactCollector collector;

   public SourceToRoughArtifactOperation(OperationLogger logger, IArtifactExtractor extractor, File sourceFile, RoughArtifactCollector collector) {
      super("Extract artifact data from source", Activator.PLUGIN_ID);
      this.extractor = extractor;
      this.sourceFile = sourceFile;
      this.collector = collector;
      this.logger = logger;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      File[] files;
      files = new File[] {sourceFile};
      double workPercentage = 1.0 / files.length;
      extractArtifacts(monitor, workPercentage, files, collector, collector.getParentRoughArtifact());
   }

   /**
    * used recursively when originally passed a directory, thus an array of files is accepted
    */
   private void extractArtifacts(IProgressMonitor monitor, double workPercentage, File[] files, RoughArtifactCollector collector, RoughArtifact parentArtifact) {
      int workAmount = calculateWork(workPercentage);
      for (File file : files) {
         if (file.isFile()) {
            processFile(file, collector, parentArtifact);
         } else if (file.isDirectory()) {
            RoughArtifact directoryArtifact = new RoughArtifact(RoughArtifactKind.CONTAINER, file.getName());
            collector.addChildRoughArtifact(directoryArtifact);
            File[] subFiles = file.listFiles(extractor.getFileFilter());
            if (files.length > 0) {
               double subPercentage = workPercentage / subFiles.length;
               extractArtifacts(monitor, subPercentage, subFiles, collector, directoryArtifact);
            }
         } else {
            throw new OseeStateException("Source location [%s] is not a valid file or directory", file);
         }
         if (monitor != null) {
            monitor.worked(workAmount);
         }
      }
   }

   private void processFile(File file, RoughArtifactCollector collector, RoughArtifact parent) {
      RoughArtifactCollector tempCollector = new RoughArtifactCollector(parent);
      try {
         extractor.process(logger, file.toURI(), tempCollector);
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
      // pass through all the collected items
      collector.addAllRoughArtifacts(tempCollector.getRoughArtifacts());
      collector.addAllRoughRelations(tempCollector.getRoughRelations());
   }
}
