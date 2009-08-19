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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughRelation;
import org.eclipse.osee.framework.skynet.core.importing.parsers.IArtifactSourceParser;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.IArtifactImportResolver;
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
   private final IArtifactSourceParser extractor;
   private final ArrayList<RoughArtifact> roughArtifacts;
   private final ArrayList<RoughRelation> roughRelations;
   private final Branch branch;
   private final Artifact importRoot;
   private final RoughArtifact rootRoughArtifact;
   private final boolean stopOnError = false;

   public ArtifactImportOperation(File file, Artifact importRoot, IArtifactSourceParser extractor, Branch branch, IArtifactImportResolver artifactResolver) throws OseeCoreException {
      super("Importing Artifacts", SkynetGuiPlugin.PLUGIN_ID);
      this.file = file;
      this.extractor = extractor;
      this.artifactResolver = artifactResolver;
      this.roughArtifacts = new ArrayList<RoughArtifact>();
      this.roughRelations = new ArrayList<RoughRelation>();
      this.branch = branch;
      this.importRoot = importRoot;
      this.rootRoughArtifact = null;
      //      new RoughArtifact(importRoot); 
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      IOperation subOperation = null;
      //      new FindRoughArtifactsOperation("Convert File(s) to Rough Artifact");
      //      doSubWork(subOperation, monitor, 0.10);

      SkynetTransaction transaction = new SkynetTransaction(branch);

      //      subOperation = new ConvertToRealArtifacts("Rough to Real Artifact(s)", transaction);
      //      doSubWork(subOperation, monitor, 0.50);

      subOperation = new ArtifactValidationCheckOperation(importRoot.getDescendants(), stopOnError);
      doSubWork(subOperation, monitor, 0.20);

      importRoot.persistAttributesAndRelations(transaction);
      monitor.subTask("Committing Transaction");
      transaction.execute();
      monitor.worked(calculateWork(0.20));
   }
}