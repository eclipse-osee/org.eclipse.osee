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

import org.eclipse.osee.coverage.event.CoverageEventManager;
import org.eclipse.osee.coverage.event.CoverageEventType;
import org.eclipse.osee.coverage.event.CoveragePackageEvent;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoverageOptionManager;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Donald G. Dunne
 */
public abstract class OseeCoverageStore extends CoverageStore {
   protected Artifact artifact;
   protected IOseeBranch branch;
   private final IArtifactType artifactType;
   private final ICoverage coverage;

   public OseeCoverageStore(ICoverage coverage, IArtifactType artifactType, IOseeBranch branch) {
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
            artifact = ArtifactTypeManager.addArtifact(artifactType, branch, null, coverage.getGuid());
         }
      }
      return artifact;

   }

   public abstract void delete(SkynetTransaction transaction, CoveragePackageEvent coverageEvent, boolean purge) throws OseeCoreException;

   @Override
   public Result save(String saveName, CoverageOptionManager coverageOptionManager) {
      try {
         SkynetTransaction transaction = TransactionManager.createTransaction(branch, "Coverage Save - " + saveName);
         CoveragePackageEvent coverageEvent = getBaseCoveragePackageEvent(CoverageEventType.Modified);
         save(transaction, coverageEvent, coverageOptionManager, artifact);
         saveTestUnitNames(transaction);
         transaction.execute();
         CoverageEventManager.instance.sendRemoteEvent(coverageEvent);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         return new Result("Save Failed: " + ex.getLocalizedMessage());
      }
      return Result.TrueResult;
   }

   @Override
   public void delete(boolean purge, String saveName) throws OseeCoreException {
      SkynetTransaction transaction = TransactionManager.createTransaction(branch, "Coverage Save - " + saveName);
      CoveragePackageEvent coverageEvent = getBaseCoveragePackageEvent(CoverageEventType.Deleted);
      delete(transaction, coverageEvent, purge);
      transaction.execute();
      CoverageEventManager.instance.sendRemoteEvent(coverageEvent);
   }

   public abstract CoveragePackageEvent getBaseCoveragePackageEvent(CoverageEventType coverageEventType);

   public abstract Result save(SkynetTransaction transaction, CoveragePackageEvent coverageEvent, CoverageOptionManager coverageOptionManager) throws OseeCoreException;

   public abstract Result save(SkynetTransaction transaction, CoveragePackageEvent coverageEvent, CoverageOptionManager coverageOptionManager, Artifact parentArt) throws OseeCoreException;

   public abstract void saveTestUnitNames(SkynetTransaction transaction) throws OseeCoreException;

   public IOseeBranch getBranch() {
      return branch;
   }
}