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
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeStateException;
import org.eclipse.osee.framework.db.connection.exception.OseeWrappedException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.ArtifactValidationCheckOperation;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

/**
 * @author Robert A. Fisher
 * @author Ryan D. Brooks
 */
public class ArtifactImportOperation extends AbstractOperation {
   private final File file;
   private final IArtifactImportResolver artifactResolver;
   private final ArtifactExtractor extractor;
   private final ArrayList<RoughArtifact> roughArtifacts;
   private final ArrayList<RoughRelation> roughRelations;
   private final Branch branch;
   private final Artifact importRoot;
   private final RoughArtifact rootRoughArtifact;
   private final boolean stopOnError = false;

   public ArtifactImportOperation(File file, Artifact importRoot, ArtifactExtractor extractor, Branch branch, IArtifactImportResolver artifactResolver) throws OseeCoreException {
      super("Importing Artifacts", SkynetGuiPlugin.PLUGIN_ID);
      this.file = file;
      this.extractor = extractor;
      this.artifactResolver = artifactResolver;
      this.roughArtifacts = new ArrayList<RoughArtifact>();
      this.roughRelations = new ArrayList<RoughRelation>();
      this.branch = branch;
      this.importRoot = importRoot;
      this.rootRoughArtifact = new RoughArtifact(importRoot);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.operation.AbstractOperation#doWork(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      IOperation subOperation = new FindRoughArtifactsOperation("Convert File(s) to Rough Artifact");
      doSubWork(subOperation, monitor, 0.10);

      SkynetTransaction transaction = new SkynetTransaction(branch);

      subOperation = new ConvertToRealArtifacts("Rough to Real Artifact(s)", transaction);
      doSubWork(subOperation, monitor, 0.50);

      subOperation = new ArtifactValidationCheckOperation(importRoot.getDescendants(), stopOnError);
      doSubWork(subOperation, monitor, 0.20);

      importRoot.persistAttributesAndRelations(transaction);
      monitor.subTask("Committing Transaction");
      transaction.execute();
      monitor.worked(calculateWork(0.20));
   }

   private final class ConvertToRealArtifacts extends AbstractOperation {
      private final SkynetTransaction transaction;

      public ConvertToRealArtifacts(String operationName, SkynetTransaction transaction) {
         super(operationName, SkynetGuiPlugin.PLUGIN_ID);
         this.transaction = transaction;
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.core.operation.AbstractOperation#doWork(org.eclipse.core.runtime.IProgressMonitor)
       */
      @Override
      protected void doWork(IProgressMonitor monitor) throws Exception {
         monitor.setTaskName("Creating Artifacts");
         int totalItems = roughArtifacts.size() + roughRelations.size();
         int unitOfWork = calculateWork(totalItems / getTotalWorkUnits());

         for (RoughArtifact roughArtifact : rootRoughArtifact.getChildren()) {
            // the getReal call will recursively call get real on all descendants of roughArtifact
            Artifact child = roughArtifact.getReal(transaction, monitor, artifactResolver);
            if (child != null) {
               importRoot.addChild(child);
            }
            monitor.worked(unitOfWork);
         }

         monitor.setTaskName("Creating Relations");
         for (RoughRelation roughRelation : roughRelations) {
            roughRelation.makeReal(transaction, monitor);
            monitor.worked(unitOfWork);
         }
      }
   }

   private final class FindRoughArtifactsOperation extends AbstractOperation {

      public FindRoughArtifactsOperation(String operationName) {
         super(operationName, SkynetGuiPlugin.PLUGIN_ID);
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.core.operation.AbstractOperation#doWork(org.eclipse.core.runtime.IProgressMonitor)
       */
      @Override
      protected void doWork(IProgressMonitor monitor) throws Exception {
         File[] files = file.isDirectory() ? file.listFiles(extractor.getFileFilter()) : new File[] {file};
         extractArtifacts(files, rootRoughArtifact);
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
                  extractor.discoverArtifactAndRelationData(file, branch);
               } catch (OseeCoreException ex) {
                  throw ex;
               } catch (Exception ex) {
                  throw new OseeWrappedException(ex);
               }
               List<RoughArtifact> tempArtifacts = extractor.getRoughArtifacts();
               roughArtifacts.addAll(tempArtifacts);
               roughRelations.addAll(extractor.getRoughRelations(parentArtifact));

               for (RoughArtifact roughArtifact : tempArtifacts) {
                  if (roughArtifact.getRoughParent() == null) {
                     parentArtifact.addChild(roughArtifact);
                  }
               }
            } else if (file.isDirectory()) {
               RoughArtifact directoryArtifact = new RoughArtifact(RoughArtifactKind.CONTAINER, branch, file.getName());
               roughArtifacts.add(directoryArtifact);
               parentArtifact.addChild(directoryArtifact);

               extractArtifacts(file.listFiles(extractor.getFileFilter()), directoryArtifact);
            } else {
               throw new OseeStateException(file + " is not a file or directory");
            }
         }
      }

   }
}