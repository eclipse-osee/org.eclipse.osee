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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeStateException;
import org.eclipse.osee.framework.db.connection.exception.OseeWrappedException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.validation.IOseeValidator;
import org.eclipse.osee.framework.skynet.core.validation.OseeValidator;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

/**
 * @author Robert A. Fisher
 * @author Ryan D. Brooks
 */
public class ArtifactImportOperation extends AbstractOperation {
   private final File file;
   private final IArtifactImportResolver artifactResolver;
   private ArtifactExtractor extractor;
   private final ArrayList<RoughArtifact> roughArtifacts;
   private final ArrayList<RoughRelation> roughRelations;
   private final Branch branch;
   private final Artifact importRoot;

   public ArtifactImportOperation(File file, Artifact importRoot, ArtifactExtractor extractor, Branch branch, IArtifactImportResolver artifactResolver) throws OseeCoreException {
      super("Importing Artifacts", 100, SkynetGuiPlugin.PLUGIN_ID);
      this.file = file;
      this.extractor = extractor;
      this.artifactResolver = artifactResolver;
      this.roughArtifacts = new ArrayList<RoughArtifact>();
      this.roughRelations = new ArrayList<RoughRelation>();
      this.branch = branch;
      this.importRoot = importRoot;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.operation.AbstractOperation#doWorkWithStatus(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   protected IStatus doWorkWithStatus(IProgressMonitor monitor) throws Exception {
      final RoughArtifact rootRoughArtifact = new RoughArtifact(importRoot);

      File[] files = file.isDirectory() ? file.listFiles(extractor.getFileFilter()) : new File[] {file};
      extractArtifacts(files, rootRoughArtifact);

      checkForCancelledStatus(monitor);

      monitor.beginTask("Creating Artifacts", roughArtifacts.size() + roughRelations.size());

      SkynetTransaction transaction = new SkynetTransaction(branch);
      for (RoughArtifact roughArtifact : rootRoughArtifact.getChildren()) {
         // the getReal call will recursively call get real on all descendants of roughArtifact
         Artifact child = roughArtifact.getReal(transaction, monitor, artifactResolver);
         if (child != null) {
            importRoot.addChild(child);
         }
      }

      monitor.setTaskName("Creating Relations");
      for (RoughRelation roughRelation : roughRelations) {
         roughRelation.makeReal(transaction, monitor);
      }

      for (Artifact artifactChanged : importRoot.getDescendants()) {
         IStatus status = OseeValidator.getInstance().validate(IOseeValidator.LONG, artifactChanged);
         if (!status.isOK()) {
            return status;
         }
      }

      checkForCancelledStatus(monitor);
      importRoot.persistAttributesAndRelations(transaction);

      monitor.setTaskName("Committing Transaction");
      monitor.subTask(""); // blank out leftover relation subtask
      monitor.worked(1); // cause the status to update
      transaction.execute();
      return Status.OK_STATUS;
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

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.operation.AbstractOperation#returnStatusFromDoWork()
    */
   @Override
   protected boolean returnStatusFromDoWork() {
      return true;
   }
}