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
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifactKind;
import org.eclipse.osee.framework.skynet.core.importing.parsers.IArtifactSourceParser;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class SourceToRoughArtifactOperation extends AbstractOperation {

   private final IArtifactSourceParser extractor;
   private final File sourceFile;
   private final RoughArtifactCollector collector;

   public SourceToRoughArtifactOperation(String operationName, IArtifactSourceParser extractor, File sourceFile, RoughArtifactCollector collector) {
      super(operationName, Activator.PLUGIN_ID);
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
      extractArtifacts(files, collector.getRootRoughArtifact());
   }

   /**
    * used recursively when originally passed a directory, thus an array of files is accepted
    * 
    * @param files
    * @param parentArtifact
    * @throws Exception
    */
   private void extractArtifacts(File[] files, RoughArtifact parentArtifact) throws OseeCoreException {
      for (File file : files) {
         if (file.isFile()) {
            try {
               extractor.process(file.toURI());
            } catch (OseeCoreException ex) {
               throw ex;
            } catch (Exception ex) {
               throw new OseeWrappedException(ex);
            }
            List<RoughArtifact> tempArtifacts = extractor.getRoughArtifacts();
            collector.addAllRoughArtifacts(tempArtifacts);
            collector.addAllRoughRelations(extractor.getRoughRelations(parentArtifact));

            for (RoughArtifact roughArtifact : tempArtifacts) {
               if (roughArtifact.getRoughParent() == null) {
                  parentArtifact.addChild(roughArtifact);
               }
            }
         } else if (file.isDirectory()) {
            RoughArtifact directoryArtifact = new RoughArtifact(RoughArtifactKind.CONTAINER, file.getName());
            collector.addRoughArtifact(directoryArtifact);
            parentArtifact.addChild(directoryArtifact);

            extractArtifacts(file.listFiles(extractor.getFileFilter()), directoryArtifact);
         } else {
            throw new OseeStateException(file + " is not a file or directory");
         }
      }
   }

}
