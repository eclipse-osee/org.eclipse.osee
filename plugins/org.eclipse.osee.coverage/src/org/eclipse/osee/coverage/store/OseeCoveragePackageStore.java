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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.osee.coverage.event.CoverageEventManager;
import org.eclipse.osee.coverage.event.CoverageEventType;
import org.eclipse.osee.coverage.event.CoveragePackageEvent;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageOptionManager;
import org.eclipse.osee.coverage.model.CoverageOptionManagerDefault;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.coverage.model.ICoverageItemProvider;
import org.eclipse.osee.coverage.model.ICoverageUnitProvider;
import org.eclipse.osee.coverage.model.IWorkProductRelatable;
import org.eclipse.osee.coverage.util.ISaveable;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.OseeData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.ElapsedTime;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * @author Donald G. Dunne
 */
public class OseeCoveragePackageStore extends OseeCoverageStore implements ISaveable {
   private final CoveragePackage coveragePackage;
   private CoverageOptionManager coverageOptionManager = CoverageOptionManagerDefault.instance();
   public static String IMPORT_RECORD_NAME = "Coverage Import Record";

   public OseeCoveragePackageStore(Artifact artifact) throws OseeCoreException {
      super(null, CoverageArtifactTypes.CoveragePackage, artifact.getBranch());
      this.artifact = artifact;
      coverageOptionManager = new CoverageOptionManagerStore(this).getCoverageOptionManager();
      this.coveragePackage =
         new CoveragePackage(artifact.getGuid(), artifact.getName(), coverageOptionManager,
            new DbWorkProductTaskProvider(this.artifact.getBranch()));
      load(coverageOptionManager);
   }

   public OseeCoveragePackageStore(CoveragePackage coveragePackage, Branch branch) {
      super(coveragePackage, CoverageArtifactTypes.CoveragePackage, branch);
      this.coveragePackage = coveragePackage;
   }

   public CoveragePackage getCoveragePackage() {
      return coveragePackage;
   }

   public static OseeCoveragePackageStore get(CoveragePackage coveragePackage, Branch branch) {
      return new OseeCoveragePackageStore(coveragePackage, branch);
   }

   public static CoveragePackage get(Artifact artifact) throws OseeCoreException {
      OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
      return packageStore.getCoveragePackage();
   }

   private void load(CoverageOptionManager coverageOptionManager) throws OseeCoreException {
      coveragePackage.clearCoverageUnits();
      if (artifact != null) {
         coveragePackage.setEditable(artifact.getSoleAttributeValue(CoreAttributeTypes.Active, true));
         for (Artifact childArt : artifact.getChildren()) {
            if (childArt.isOfType(CoverageArtifactTypes.CoverageUnit, CoverageArtifactTypes.CoverageFolder)) {
               coveragePackage.addCoverageUnit(OseeCoverageUnitStore.get(coveragePackage, childArt,
                  coverageOptionManager));
            }
         }
      }
      loadWorkProductTaskNames(coveragePackage.getCoverageUnits());
   }

   public void loadWorkProductTaskNames(Collection<? extends ICoverage> coverages) {
      this.coveragePackage.getWorkProductTaskProvider().reload();
      for (ICoverage coverage : coverages) {
         if (coverage instanceof IWorkProductRelatable) {
            if (Strings.isValid(((IWorkProductRelatable) coverage).getWorkProductTaskGuid())) {
               ((IWorkProductRelatable) coverage).setWorkProductTask(this.coveragePackage.getWorkProductTaskProvider().getWorkProductTask(
                  ((IWorkProductRelatable) coverage).getWorkProductTaskGuid()));
            }
         }
         if (coverage instanceof ICoverageUnitProvider) {
            loadWorkProductTaskNames(((ICoverageUnitProvider) coverage).getCoverageUnits());
         }
         if (coverage instanceof ICoverageItemProvider) {
            loadWorkProductTaskNames(((ICoverageItemProvider) coverage).getCoverageItems());
         }
      }
   }

   @Override
   public Result save(SkynetTransaction transaction, CoveragePackageEvent coverageEvent) throws OseeCoreException {
      boolean newCoveragePackage = getArtifact(false) == null;
      getArtifact(true);
      ElapsedTime elapsedTime = new ElapsedTime(getClass().getSimpleName() + " - save");
      artifact.setName(coveragePackage.getName());
      coverageEvent.getPackage().setEventType(newCoveragePackage ? CoverageEventType.Added : CoverageEventType.Modified);
      artifact.setSoleAttributeValue(CoreAttributeTypes.Active, coveragePackage.isEditable().isTrue());
      for (CoverageUnit coverageUnit : coveragePackage.getCoverageUnits()) {
         OseeCoverageUnitStore store = new OseeCoverageUnitStore(coverageUnit, artifact.getBranch());
         store.save(transaction, coverageEvent);
         Artifact childArt = store.getArtifact(false);
         if (childArt.getParent() == null && !artifact.getChildren().contains(childArt)) {
            artifact.addChild(store.getArtifact(false));
         }
      }
      artifact.persist(transaction);
      elapsedTime.end();
      return Result.TrueResult;
   }

   public Result save(SkynetTransaction transaction, CoveragePackageEvent coverageEvent, Collection<ICoverage> coverages) throws OseeCoreException {
      ElapsedTime elapsedTime = new ElapsedTime(getClass().getSimpleName() + " - save(coverages)");
      for (ICoverage coverage : coverages) {
         CoverageUnit coverageUnit = null;
         if (coverage instanceof CoverageItem) {
            coverageUnit = ((CoverageItem) coverage).getCoverageUnit();
         } else if (coverage instanceof CoverageUnit) {
            coverageUnit = (CoverageUnit) coverage;
         } else {
            throw new OseeArgumentException("Unhandled coverage type");
         }
         OseeCoverageUnitStore store = new OseeCoverageUnitStore(coverageUnit, transaction.getBranch());
         store.save(transaction, coverageEvent);
      }
      elapsedTime.end();
      return Result.TrueResult;
   }

   @Override
   public Result saveImportRecord(SkynetTransaction transaction, CoverageImport coverageImport) throws OseeCoreException {
      if (coverageImport == null) {
         return Result.FalseResult;
      }
      Artifact importRecordArt = null;
      for (Artifact artifact : getArtifact(false).getChildren()) {
         if (artifact.getName().equals(IMPORT_RECORD_NAME)) {
            importRecordArt = artifact;
            break;
         }
      }
      if (importRecordArt == null) {
         importRecordArt =
            ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralDocument, artifact.getBranch(), IMPORT_RECORD_NAME);
         // must set the extension before setting content
         importRecordArt.setSoleAttributeFromString(CoreAttributeTypes.Extension, "zip");
         getArtifact(false).addChild(importRecordArt);
         getArtifact(false).persist(transaction);
      }
      importRecordArt.setSoleAttributeFromStream(CoreAttributeTypes.NativeContent, getInputStream(coverageImport));
      importRecordArt.persist(transaction);
      return Result.TrueResult;
   }

   public static InputStream getInputStream(CoverageImport coverageImport) {
      try {
         File zipFile = OseeData.getFile("coverage.zip");
         Lib.compressFiles(coverageImport.getImportDirectory(), coverageImport.getImportRecordFiles(),
            zipFile.getAbsolutePath());
         return new FileInputStream(zipFile);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return null;
   }

   @Override
   public void delete(SkynetTransaction transaction, CoveragePackageEvent coverageEvent, boolean purge) throws OseeCoreException {
      if (getArtifact(false) != null) {
         if (purge) {
            getArtifact(false).purgeFromBranch();
         } else {
            getArtifact(false).deleteAndPersist(transaction);
         }
      }
      for (CoverageUnit childCoverageUnit : coveragePackage.getCoverageUnits()) {
         new OseeCoverageUnitStore(childCoverageUnit, transaction.getBranch()).delete(transaction, coverageEvent, purge);
      }
   }

   @Override
   public Result isEditable() {
      return coveragePackage.isEditable();
   }

   public static Collection<Artifact> getCoveragePackageArtifacts(Branch branch) throws OseeCoreException {
      return ArtifactQuery.getArtifactListFromType(CoverageArtifactTypes.CoveragePackage, branch);
   }

   @Override
   public Result save(Collection<ICoverage> coverages) {
      try {
         SkynetTransaction transaction = new SkynetTransaction(branch, "Coverage Save");
         CoveragePackageEvent coverageEvent = new CoveragePackageEvent(coveragePackage, CoverageEventType.Modified);
         save(transaction, coverageEvent, coverages);
         transaction.execute();
         CoverageEventManager.getInstance().sendRemoteEvent(coverageEvent);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return new Result("Save Failed: " + ex.getLocalizedMessage());
      }
      return Result.TrueResult;

   }

   public CoverageOptionManager getCoverageOptionManager() {
      return coverageOptionManager;
   }

   public void setCoverageOptionManager(CoverageOptionManager coverageOptionManager) {
      this.coverageOptionManager = coverageOptionManager;
   }

   @Override
   public CoveragePackageEvent getBaseCoveragePackageEvent(CoverageEventType coverageEventType) {
      return new CoveragePackageEvent(coveragePackage, coverageEventType);
   }
}
