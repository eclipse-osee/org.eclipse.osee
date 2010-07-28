/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.store;

import java.util.logging.Level;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoverageOptionManager;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * @author Donald G. Dunne
 */
public abstract class OseeCoverageStore extends CoverageStore {
   protected Artifact artifact;
   protected Branch branch;
   private final IArtifactType artifactType;
   private final ICoverage coverage;

   public OseeCoverageStore(ICoverage coverage, IArtifactType artifactType, Branch branch) {
      super(coverage);
      this.coverage = coverage;
      this.artifactType = artifactType;
      this.branch = branch;
   }

   public Artifact getArtifact(boolean create) throws OseeCoreException {
      if (artifact == null) {
         try {
            artifact = ArtifactQuery.getArtifactFromId(coverage.getGuid(), branch);
         } catch (ArtifactDoesNotExist ex) {
            // do nothing
         }
         if (artifact == null && create) {
            artifact = ArtifactTypeManager.addArtifact(artifactType, branch, coverage.getGuid(), null);
         }
      }
      return artifact;

   }

   public abstract void delete(SkynetTransaction transaction, boolean purge) throws OseeCoreException;

   @Override
   public abstract void load(CoverageOptionManager coverageOptionManager) throws OseeCoreException;

   @Override
   public Result save() {
      try {
         SkynetTransaction transaction = new SkynetTransaction(branch, "Coverage Save");
         save(transaction);
         transaction.execute();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return new Result("Save Failed: " + ex.getLocalizedMessage());
      }
      return Result.TrueResult;
   }

   @Override
   public void delete(boolean purge) throws OseeCoreException {
      SkynetTransaction transaction = new SkynetTransaction(branch, "Coverage Save");
      delete(transaction, purge);
      transaction.execute();
   }

   public abstract Result save(SkynetTransaction transaction) throws OseeCoreException;

   public Branch getBranch() {
      return branch;
   }

}
