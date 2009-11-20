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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifactKind;
import org.eclipse.osee.framework.skynet.core.importing.parsers.IArtifactExtractor;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class SourceToRoughArtifactOperation extends AbstractOperation {

   private final IArtifactExtractor extractor;
   private final File sourceFile;
   private final RoughArtifactCollector collector;

   public SourceToRoughArtifactOperation(IArtifactExtractor extractor, File sourceFile, RoughArtifactCollector collector) {
      super("Extract artifact data from source", Activator.PLUGIN_ID);
      this.extractor = extractor;
      this.sourceFile = sourceFile;
      this.collector = collector;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      File[] files;
      if (sourceFile.isDirectory()) {
         files = sourceFile.listFiles(extractor.getFileFilter());
      } else {
         files = new File[] {sourceFile};
      }
      double workPercentage = 1.0 / files.length;
      extractArtifacts(monitor, workPercentage, files, collector, collector.getParentRoughArtifact());
   }

   /**
    * used recursively when originally passed a directory, thus an array of files is accepted
    */
   private void extractArtifacts(IProgressMonitor monitor, double workPercentage, File[] files, RoughArtifactCollector collector, RoughArtifact parentArtifact) throws OseeCoreException {
      int workAmount = calculateWork(workPercentage);
      for (File file : files) {
         if (file.isFile()) {
            processFile(file, collector, parentArtifact);
         } else if (file.isDirectory()) {
            RoughArtifact directoryArtifact = new RoughArtifact(RoughArtifactKind.CONTAINER, file.getName());
            collector.addRoughArtifact(directoryArtifact);
            File[] subFiles = file.listFiles(extractor.getFileFilter());
            if (files.length > 0) {
               double subPercentage = workPercentage / subFiles.length;
               extractArtifacts(monitor, subPercentage, subFiles, collector, directoryArtifact);
            }
         } else {
            throw new OseeStateException("Source location \"" + file + "\" is not a valid file or directory");
         }
         monitor.worked(workAmount);
      }
   }

   private void processFile(File file, RoughArtifactCollector collector, RoughArtifact parent) throws OseeCoreException {
      RoughArtifactCollector tempCollector = new RoughArtifactCollector(parent);
      try {
         extractor.process(file.toURI(), tempCollector);
      } catch (OseeCoreException ex) {
         throw ex;
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
      // pass through all the collected items
      collector.addAllRoughArtifacts(tempCollector.getRoughArtifacts());
      collector.addAllRoughRelations(tempCollector.getRoughRelations());
   }

}
