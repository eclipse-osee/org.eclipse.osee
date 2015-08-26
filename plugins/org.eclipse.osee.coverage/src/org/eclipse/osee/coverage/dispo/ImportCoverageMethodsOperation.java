/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.dispo;

import java.awt.Desktop;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.coverage.action.ConfigureCoverageMethodsAction;
import org.eclipse.osee.coverage.action.ShowMergeReportAction;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageOption;
import org.eclipse.osee.coverage.model.CoverageOptionManager;
import org.eclipse.osee.coverage.model.CoverageOptionManagerDefault;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.coverage.model.ICoverageItemProvider;
import org.eclipse.osee.coverage.model.ICoverageUnitProvider;
import org.eclipse.osee.coverage.model.ITestUnitProvider;
import org.eclipse.osee.coverage.store.OseeCoveragePackageStore;
import org.eclipse.osee.coverage.store.OseeCoverageUnitStore;
import org.eclipse.osee.coverage.store.TestUnitCache;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.results.html.XResultPage.Manipulations;

public class ImportCoverageMethodsOperation extends org.eclipse.osee.framework.core.operation.AbstractOperation {
   private final List<String> autoDispos = Arrays.asList(CoverageOptionManagerDefault.Exception_Handling.name,
      CoverageOptionManagerDefault.Not_Covered.name, CoverageOptionManagerDefault.Test_Unit.name);
   private final Artifact fromPackageArt;
   private final Artifact toPackageArt;
   private final boolean isPersistTransaction;
   private final String resultsDir;
   private final boolean isRetainTaskTracking;
   private final boolean forceCompareMethodNumbers;

   public ImportCoverageMethodsOperation(Artifact fromPackageArt, Artifact toPackageArt, String resultsDir, boolean isPersistTransaction, boolean isRetainTaskTracking, boolean forceCompareMethodNumbers) {
      super("Import Coverage Methods for " + fromPackageArt, Activator.PLUGIN_ID);
      this.fromPackageArt = fromPackageArt;
      this.toPackageArt = toPackageArt;
      this.resultsDir = resultsDir;
      this.isPersistTransaction = isPersistTransaction;
      this.isRetainTaskTracking = isRetainTaskTracking;
      this.forceCompareMethodNumbers = forceCompareMethodNumbers;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {

      XResultData data = new XResultData(false);
      try {

         if (!Strings.isValid(resultsDir)) {
            throw new OseeArgumentException("Invalid Results Dir");
         }
         File file = new File(resultsDir);
         if (!file.exists()) {
            file.mkdir();
         }

         @SuppressWarnings("unused")
         Set<Artifact> artifactLoadCache = ConfigureCoverageMethodsAction.bulkLoadCoveragePackage(fromPackageArt);
         OseeCoveragePackageStore fromPackageStore = new OseeCoveragePackageStore(fromPackageArt);
         CoveragePackage fromPackage = fromPackageStore.getCoveragePackage();
         int numCoverageUnits = fromPackage.getCoverageUnitCount(true);
         monitor.beginTask(getName(), numCoverageUnits);

         @SuppressWarnings("unused")
         Set<Artifact> artifactLoadCache2 = ConfigureCoverageMethodsAction.bulkLoadCoveragePackage(toPackageArt);
         OseeCoveragePackageStore toPackageStore = new OseeCoveragePackageStore(toPackageArt);
         CoveragePackage toPackage = toPackageStore.getCoveragePackage();

         String title =
            String.format("Merging dispositions from [%s] to [%s]\n\n", fromPackage.getName(), toPackage.getName());
         ImportCoverageMethodsCounter counter = new ImportCoverageMethodsCounter();

         // Merge Dispositions
         processDispositionsRecurse(monitor, counter, data, fromPackage, toPackage);
         ITestUnitProvider fromProvider = OseeCoverageUnitStore.getTestUnitProvider(fromPackageArt, null);
         ITestUnitProvider toProvider = OseeCoverageUnitStore.getTestUnitProvider(toPackageArt, null);

         if (fromProvider instanceof TestUnitCache && toProvider instanceof TestUnitCache) {
            TestUnitCache toTUC = (TestUnitCache) toProvider;
            TestUnitCache fromTUC = (TestUnitCache) fromProvider;
            toTUC.merge(fromTUC);
         }

         data.log("\n\nTotals: " + counter.toString());
         data.log(title);

         // Persist Merge
         if (isPersistTransaction) {
            data.log("Persisting...");
            OseeCoveragePackageStore persistStore =
               new OseeCoveragePackageStore(toPackage, BranchManager.getBranch(toPackageArt.getBranch()));
            Result results = persistStore.save(title, toPackageStore.getCoverageOptionManager());
            if (results.isFalse()) {
               data.errorf("Error persisting [%s]", results.toString());
            }
         }

         // Set WorkProductPcrGuid if retain tracking
         if (isRetainTaskTracking) {
            toPackage.getWorkProductTaskProvider().addWorkProductAction(
               fromPackage.getWorkProductTaskProvider().getWorkProductRelatedActions());
         }

         // Display results in OSEE and results dir
         data.log("Complete\n\nResults at " + resultsDir);
         String html =
            XResultDataUI.report(data, "Merge Dispositions", Manipulations.GUID_CMD_HYPER, Manipulations.ERROR_RED,
               Manipulations.CONVERT_NEWLINES, Manipulations.WARNING_YELLOW, Manipulations.ERROR_WARNING_HEADER);
         Lib.writeStringToFile(html, new File(resultsDir + File.separator + "results.html"));

         // Save off merge report in related action
         if (isPersistTransaction) {
            Artifact importReportArt = null;
            for (Artifact art : toPackageArt.getRelatedArtifacts(CoreRelationTypes.SupportingInfo_SupportedBy)) {
               if (art.getName().equals("Import Coverage Method Report")) {
                  importReportArt = art;
                  break;
               }
            }
            if (importReportArt == null) {
               importReportArt =
                  ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralDocument, toPackageArt.getBranch(),
                     "Import Coverage Method Report");
               importReportArt.setSoleAttributeValue(CoreAttributeTypes.Extension, "html");
               toPackageArt.addRelation(CoreRelationTypes.SupportingInfo_SupportedBy, importReportArt);
            }
            importReportArt.setSoleAttributeFromString(CoreAttributeTypes.NativeContent, html);
            importReportArt.persist(String.format("Import Coverage Method Report for [%s]", toPackage.getName()));
         }

         // Open results dir
         if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            desktop.open(new File(resultsDir));
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         data.errorf("Exception [%s] (see log)", ex.getLocalizedMessage());
         XResultDataUI.report(data, "Merge Dispositions - Error");
      } finally {
         monitor.done();
      }
   }

   private void processDispositionsRecurse(IProgressMonitor monitor, ImportCoverageMethodsCounter counter, XResultData data, ICoverage fromCoverage, CoveragePackage toPackage) throws OseeCoreException {
      //      String onlyProcessCoverageFile = "copr_crew_input.update.2.ada"; // Uncomment to debug single file
      String onlyProcessCoverageFile = "";
      if (fromCoverage instanceof CoverageItem) {
         counter.numItems++;
         CoverageItem fromCoverageItem = (CoverageItem) fromCoverage;
         if (isManualDisp(fromCoverageItem)) {
            counter.numDispo++;
            data.logf("%s - Merge disp [%s] ", CoverageUtil.getFullPath(fromCoverageItem, false),
               fromCoverageItem.getCoverageMethod().name);
            importDisposition(counter, data, fromCoverageItem, toPackage);
         }
      }
      if (fromCoverage instanceof ICoverageUnitProvider) {
         ICoverageUnitProvider unitProvider = (ICoverageUnitProvider) fromCoverage;
         for (CoverageUnit unit : unitProvider.getCoverageUnits()) {
            if (Strings.isValid(onlyProcessCoverageFile) && ShowMergeReportAction.isFile(unit) && !unit.getName().equals(
               onlyProcessCoverageFile)) {
               continue;
            }
            System.out.println("Merging " + unit.getName());
            processDispositionsRecurse(monitor, counter, data, unit, toPackage);
         }
      }
      if (fromCoverage instanceof ICoverageItemProvider) {
         ICoverageItemProvider itemProvider = (ICoverageItemProvider) fromCoverage;
         for (CoverageItem unit : itemProvider.getCoverageItems()) {
            processDispositionsRecurse(monitor, counter, data, unit, toPackage);
         }
      }
      if (fromCoverage instanceof CoverageUnit) {
         monitor.worked(1);
      }
   }

   private void importDisposition(ImportCoverageMethodsCounter counter, XResultData data, CoverageItem fromItem, CoveragePackage toPackage) throws OseeCoreException {
      // First, attempt to find this coverage item
      ImportMatch matchItem = findMatch(fromItem, toPackage);
      if (matchItem.isMatch()) {
         counter.numMatch++;
         data.logf("MATCH [%s][%s]", matchItem.getFromItem().getOrderNumber(),
            matchItem.getToItem().getOrderNumber());
         CoverageItem toItem = (CoverageItem) matchItem.getToItem();
         if (toItem.getCoverageMethod().name.equals(CoverageOptionManager.Test_Unit.name) || toItem.getCoverageMethod().name.equals(CoverageOptionManager.Exception_Handling.name)) {
            data.logf(" - KEEP CURRENT [%s]\n", toItem.getCoverageMethod().name);
         } else if (toItem.getCoverageMethod().getName().equals(fromItem.getCoverageMethod().getName())) {
            data.log(" - ALREADY SET");
         } else {
            counter.numImported++;
            data.log(" - IMPORTED");

            CoverageOptionManager coverageOptionManager = toPackage.getCoverageOptionManager();
            CoverageOption coverageOption = fromItem.getCoverageMethod();

            if (!checkOptionIsInPackage(coverageOptionManager, coverageOption)) {
               throw new OseeCoreException(String.format(
                  "The Coverage Method [%s] does not exist in the Coverage Package [%s]", coverageOption.getName(),
                  toPackage.getName()));
            }

            toItem.setCoverageMethod(fromItem.getCoverageMethod());
            if (!toItem.getRationale().equals(fromItem.getRationale())) {
               data.logf("   --> Updated notes from [%s] to [%s]\n", toItem.getRationale(),
                  fromItem.getRationale());
               toItem.setRationale(fromItem.getRationale());
            }
            if (isRetainTaskTracking) {
               String toTaskGuid = toItem.getWorkProductTaskGuid();
               if (toTaskGuid == null) {
                  toTaskGuid = "";
               }
               String fromTaskGuid = fromItem.getWorkProductTaskGuid();
               if (!toTaskGuid.equals(fromTaskGuid)) {
                  data.logf("   --> Updated task from [%s][%s] to [%s][%s]\n", toTaskGuid,
                     toItem.getWorkProductTaskStr(), fromTaskGuid, fromItem.getWorkProductTaskStr());
                  toItem.setWorkProductTaskGuid(fromTaskGuid);
               }
            }
         }

      } else {
         counter.numNoMatch++;
         CoverageUnit fromFile = getFileCoverageUnit(fromItem);
         if (!counter.fileToErrorCount.contains(fromFile.getName())) {
            outputFileToResults(resultsDir, "from", fromFile);
            CoverageUnit matchingToFile = getFileCoverageUnitFromNameRecurseDown(toPackage, fromFile.getName());
            if (matchingToFile != null) {
               outputFileToResults(resultsDir, "to", matchingToFile);
            }
         }
         counter.fileToErrorCount.put(fromFile.getName());
         data.errorf("NO MATCH [%s]", matchItem.getDescription());
         data.logf("   --> Notes [%s]\n", getNonNullValue(fromItem.getNotes()));
         data.logf("   --> Task [%s][%s]\n", getNonNullValue(fromItem.getWorkProductTaskGuid()),
            getNonNullValue(fromItem.getWorkProductTaskStr()));
      }
   }

   private boolean checkOptionIsInPackage(CoverageOptionManager coverageOptionManager, CoverageOption coverageMethod) {
      boolean answer;
      CoverageOption coverageOption = coverageOptionManager.get(coverageMethod.getName());
      if (coverageOption == null) {
         answer = false;
      } else {
         answer = true;
      }
      return answer;
   }

   private String getPkgDir(String baseDir, String dirName) {
      String dir = baseDir + System.getProperty("file.separator") + dirName;
      File file = new File(dir);
      if (!file.exists()) {
         file.mkdir();
      }
      return dir;
   }

   private void outputFileToResults(String baseDir, String dirName, CoverageUnit unit) throws OseeCoreException {
      String unitDirName = getPkgDir(baseDir, dirName);
      ShowMergeReportAction.createCoverageUnitFile(unitDirName, unit);
   }

   private String getNonNullValue(String value) {
      return value == null ? "" : value;
   }

   private ImportMatch findMatch(CoverageItem fromItem, CoveragePackage toPackage) {
      CoverageUnit fromFile = getFileCoverageUnit(fromItem);
      CoverageUnit toFile = getFileCoverageUnitFromNameRecurseDown(toPackage, fromFile.getName());
      if (toFile == null) {
         return new ImportMatch(ImportMatchType.NoMatch, fromFile, null, "Can't locate file [%s] in new package",
            fromFile.getName());
      }
      CoverageUnit fromMethod = getMethodCoverageUnit(fromFile, fromItem);
      if (fromMethod == null) {
         return new ImportMatch(ImportMatchType.NoMatch, fromFile, null, "Can't locate FROM method [%s]",
            fromItem.getParent().getName());
      }

      List<CoverageUnit> toMethods = getToMethodCoverageUnitsFromName(toFile, fromMethod.getName());
      if (toMethods.isEmpty()) {
         return new ImportMatch(ImportMatchType.NoMatch, fromFile, null, "Can't locate TO method [%s]",
            fromMethod.getName());
      }

      CoverageUnit bestFitToMethod = null;
      String description = "";
      List<CoverageUnit> strongUnits = new ArrayList<CoverageUnit>();
      List<CoverageUnit> medUnits = new ArrayList<CoverageUnit>();
      List<CoverageUnit> weakUnits = new ArrayList<CoverageUnit>();
      for (CoverageUnit possibleToMethod : toMethods) {
         boolean isOrderEqual = isOrderEqual(fromMethod, possibleToMethod);
         boolean isChildrenSizeEqual = isChildrenSizeEqual(fromMethod, possibleToMethod);
         if (isOrderEqual && isChildrenSizeEqual) {
            description += "Method match STRONG (name, order and num children); ";
            strongUnits.add(possibleToMethod);
         } else if (isOrderEqual) {
            description += "Method match MEDIUM (name and order); ";
            medUnits.add(possibleToMethod);
         } else if (isChildrenSizeEqual) {
            description += "Method match WEAK (name and num children); ";
            weakUnits.add(possibleToMethod);
         } else {
            description += "Method match WEAK (name only); ";
            weakUnits.add(possibleToMethod);
         }
      }
      description +=
         " STRONG=" + strongUnits.size() + " MEDIUM=" + medUnits.size() + " WEAK=" + weakUnits.size() + "; ";
      if (strongUnits.size() == 1) {
         bestFitToMethod = strongUnits.iterator().next();
         description += "picked STRONG; ";
      } else if (medUnits.size() == 1) {
         bestFitToMethod = medUnits.iterator().next();
         description += "picked MED; ";
      } else if (weakUnits.size() == 1) {
         bestFitToMethod = weakUnits.iterator().next();
         description += "picked WEAK; ";
      }
      // TODO may want to handle case where 2 strong or med; see if item name / order match

      if (bestFitToMethod == null) {
         return new ImportMatch(ImportMatchType.NoMatch, fromFile, null, "No TO method MATCH [%s] - [%s]",
            fromMethod.getName(), description);
      }

      List<CoverageItem> toCoverageItems = bestFitToMethod.getCoverageItems();
      CoverageItem toItem = null;
      for (CoverageItem toCoverageItem : toCoverageItems) {
         boolean orderMatch = toCoverageItem.getOrderNumber().equals(fromItem.getOrderNumber());
         boolean nameMatch = toCoverageItem.getName().equals(fromItem.getName());
         if (orderMatch && nameMatch) {
            toItem = toCoverageItem;
            description += "Item Match (name and order)";
            break;
         }
      }
      if (toItem == null) {
         return new ImportMatch(ImportMatchType.NoMatch, fromFile, null, "No TO item MATCH [%s] - [%s]",
            fromMethod.getName(), description);
      }
      return new ImportMatch(ImportMatchType.Match, fromItem, toItem, "MATCH [%s]", toItem, description);
   }

   private boolean isOrderEqual(CoverageUnit coverage1, CoverageUnit coverage2) {
      return (coverage1.getOrderNumber().equals(coverage2.getOrderNumber()));
   }

   private boolean isChildrenSizeEqual(CoverageUnit coverage1, CoverageUnit coverage2) {
      return (coverage1.getChildren().size() == coverage2.getChildren().size());
   }

   private List<CoverageUnit> getToMethodCoverageUnitsFromName(CoverageUnit toFile, String matchName) {
      List<CoverageUnit> units = new ArrayList<CoverageUnit>();
      for (CoverageUnit unit : toFile.getCoverageUnits()) {
         if (unit.getName().equals(matchName)) {
            units.add(unit);
         }
      }
      return units;
   }

   private CoverageUnit getMethodCoverageUnit(CoverageUnit fromFile, CoverageItem fromItem) {
      CoverageUnit ret = null;
      for (CoverageUnit unit : fromFile.getCoverageUnits()) {
         if (methodsMatched(unit, fromItem, forceCompareMethodNumbers)) {
            ret = unit;
            break;
         }
      }
      return ret;
   }

   private boolean methodsMatched(CoverageUnit cvgUnitA, CoverageItem cvgUnitB, boolean forceCompareMethodNumbers) {
      ICoverage cvgUnitBParent = cvgUnitB.getParent();
      boolean result = cvgUnitA.getName().equals(cvgUnitBParent.getName());
      if (forceCompareMethodNumbers) {
         result &= cvgUnitA.getOrderNumber().equals(cvgUnitBParent.getOrderNumber());
      }
      return result;
   }

   private CoverageUnit getFileCoverageUnitFromNameRecurseDown(ICoverage coverage, String name) {
      if (coverage instanceof ICoverageUnitProvider) {
         for (CoverageUnit unit : ((ICoverageUnitProvider) coverage).getCoverageUnits()) {
            if (ShowMergeReportAction.isFile(unit) && unit.getName().equals(name)) {
               return unit;
            }
         }
      }
      for (CoverageUnit unit : ((ICoverageUnitProvider) coverage).getCoverageUnits()) {
         CoverageUnit resultUnit = getFileCoverageUnitFromNameRecurseDown(unit, name);
         if (resultUnit != null) {
            return resultUnit;
         }
      }
      return null;
   }

   private CoverageUnit getFileCoverageUnit(ICoverage coverage) {
      if (ShowMergeReportAction.isFile(coverage)) {
         return (CoverageUnit) coverage;
      }
      ICoverage parentCoverage = coverage.getParent();
      if (parentCoverage == null) {
         return null;
      }
      return getFileCoverageUnit(parentCoverage);
   }

   private boolean isManualDisp(CoverageItem item) {
      return !autoDispos.contains(item.getCoverageMethod().name);
   }

}
