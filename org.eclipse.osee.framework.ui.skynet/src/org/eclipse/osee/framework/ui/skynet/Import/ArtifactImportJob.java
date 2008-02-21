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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

/**
 * @author Robert A. Fisher
 */
public class ArtifactImportJob extends Job {
   private static final ArtifactPersistenceManager artifactManager = ArtifactPersistenceManager.getInstance();
   private static final ConfigurationPersistenceManager configurationManager =
         ConfigurationPersistenceManager.getInstance();
   private final File file;
   private final ArtifactSubtypeDescriptor folderDescriptor;
   private final IArtifactImportResolver artifactResolver;
   private ArtifactExtractor extractor;
   private final ArrayList<RoughArtifact> roughArtifacts;
   private final ArrayList<RoughRelation> roughRelations;
   private final Branch branch;
   private final Artifact importRoot;

   public ArtifactImportJob(File file, Artifact importRoot, ArtifactExtractor extractor, Branch branch, IArtifactImportResolver artifactResolver) throws IllegalArgumentException, SQLException {
      super("Importing");

      this.file = file;
      this.extractor = extractor;
      this.folderDescriptor = configurationManager.getArtifactSubtypeDescriptor("Folder");
      this.artifactResolver = artifactResolver;
      this.roughArtifacts = new ArrayList<RoughArtifact>();
      this.roughRelations = new ArrayList<RoughRelation>();
      this.branch = branch;
      this.importRoot = importRoot;
   }

   public IStatus run(final IProgressMonitor monitor) {
      IStatus toReturn;
      try {
         final RoughArtifact rootRoughArtifact = new RoughArtifact(importRoot);
         extractArtifacts(new File[] {file}, rootRoughArtifact);

         if (monitor.isCanceled()) {
            return new Status(Status.CANCEL, SkynetGuiPlugin.PLUGIN_ID, "User Cancled the operation.");
         }

         monitor.beginTask("Creating Artifacts", roughArtifacts.size() + roughRelations.size());

         AbstractSkynetTxTemplate txWrapper = new AbstractSkynetTxTemplate(branch) {
            @Override
            protected void handleTxWork() throws Exception {
               for (RoughArtifact roughArtifact : rootRoughArtifact.getChildren()) {
                  // the getReal call with recursively call get real on all descendants of roughArtifact
                  importRoot.addChild(roughArtifact.getReal(branch, monitor, artifactResolver));
               }

               monitor.setTaskName("Creating Relations");
               for (RoughRelation roughRelation : roughRelations) {
                  roughRelation.makeReal(branch, monitor);
               }

               artifactManager.setProgressMonitor(monitor);
               importRoot.persist(true);
               artifactManager.setProgressMonitor(null);

               monitor.setTaskName("Committing Transaction");
               monitor.subTask(""); // blank out leftover relation subtask
               monitor.worked(1); // cause the status to update
            }
         };
         txWrapper.execute();
         toReturn = Status.OK_STATUS;
      } catch (Exception ex) {
         toReturn = new Status(Status.ERROR, SkynetGuiPlugin.PLUGIN_ID, -1, ex.getMessage(), ex);
      } finally {
         monitor.done();
      }
      return toReturn;
   }

   /**
    * used recursively when originally passed a directory, thus an array of files is accepted
    * 
    * @param files
    * @param parentArtifact
    * @throws Exception
    */
   private void extractArtifacts(File[] files, RoughArtifact parentArtifact) throws Exception {
      for (File file : files) {
         if (file.isFile()) {
            extractor.discoverArtifactAndRelationData(file);
            List<RoughArtifact> tempArtifacts = extractor.getRoughArtifacts();
            roughArtifacts.addAll(tempArtifacts);
            roughRelations.addAll(extractor.getRoughRelations(parentArtifact));

            for (RoughArtifact roughArtifact : tempArtifacts) {
               if (roughArtifact.getRoughParent() == null) {
                  parentArtifact.addChild(roughArtifact);
               }
            }
         } else if (file.isDirectory()) {
            RoughArtifact directoryArtifact = new RoughArtifact(branch, file.getName());
            directoryArtifact.setHeadingDescriptor(folderDescriptor);
            directoryArtifact.setPrimaryDescriptor(folderDescriptor);
            roughArtifacts.add(directoryArtifact);
            parentArtifact.addChild(directoryArtifact);

            extractArtifacts(file.listFiles(extractor.getFileFilter()), directoryArtifact);
         } else {
            throw new IllegalStateException(file + " is not a file or directory");
         }
      }
   }
}
