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
package org.eclipse.osee.coverage.action;

import java.io.File;
import java.io.IOException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.coverage.editor.xmerge.XCoverageMergeViewer;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.merge.MatchItem;
import org.eclipse.osee.coverage.merge.MergeItem;
import org.eclipse.osee.coverage.merge.MergeItemGroup;
import org.eclipse.osee.coverage.merge.MergeManager;
import org.eclipse.osee.coverage.merge.MergeType;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.model.CoveragePackageBase;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryEntryDialog;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.program.Program;

/**
 * @author Donald G. Dunne
 */
public class ShowMergeReportAction extends Action {
   private static final String COVERAGE_COMPARE_EXEC = "coverage.merge.compare.exec";
   private static final String COVERAGE_MERGE_BASEPATH = "coverage.merge.report.basepath";
   private CoveragePackage coveragePackage;
   private CoverageImport coverageImport;
   private XCoverageMergeViewer importXViewer;
   private String baseDir;
   private String compareExec;

   public ShowMergeReportAction() {
      super("Show Merge Report", IAction.AS_PUSH_BUTTON);
   }

   public static void getCoverageUnitContentsRecurse(StringBuilder contents, ICoverage coverage) throws OseeCoreException {
      if (coverage instanceof CoverageUnit) {
         CoverageUnit unit = (CoverageUnit) coverage;
         contents.append("   " + coverage.getName() + System.getProperty("line.separator"));
         for (ICoverage child : unit.getCoverageUnitsOrdered()) {
            getCoverageUnitContentsRecurse(contents, child);
         }
         for (ICoverage child : unit.getCoverageItemsOrdered()) {
            getCoverageUnitContentsRecurse(contents, child);
         }
      } else if (coverage instanceof CoverageItem) {
         CoverageItem item = (CoverageItem) coverage;
         contents.append("      " + coverage.getName() + System.getProperty("line.separator"));
         contents.append("       > Method: " + item.getCoverageMethod().name + System.getProperty("line.separator"));
         //         contents.append("       > Units: " + item.getTestUnits() + System.getProperty("line.separator"));
         //         contents.append("       > Notes: " + item.getNotes() + System.getProperty("line.separator"));
      }
   }

   private String recurseCreateReport(String baseDir, StringBuilder resultStr, ICoverage coverage) throws OseeCoreException {
      String folder = "";
      if (coverage instanceof CoveragePackageBase) {
         CoveragePackageBase packageBase = (CoveragePackageBase) coverage;
         for (CoverageUnit unit : packageBase.getCoverageUnits()) {
            recurseCreateReport(baseDir, resultStr, unit);
         }

      } else if (coverage instanceof CoverageUnit) {
         CoverageUnit unit = (CoverageUnit) coverage;
         if (unit.isFolder()) {
            folder = getOrCreateFolder(baseDir, unit);
         } else if (isFile(unit)) {
            createCoverageUnitFile(baseDir, unit);
         } else {
            throw new OseeStateException("Unexpected coverage type2: " + coverage);
         }
         for (ICoverage child : unit.getCoverageUnits()) {
            if (!isFile(coverage)) {
               recurseCreateReport(folder, resultStr, child);
            }
         }
      } else {
         throw new OseeStateException("Unexpected coverage type: " + coverage);
      }
      return folder;
   }

   public static void createCoverageUnitFile(String baseDir, CoverageUnit unit) throws OseeCoreException {
      try {
         StringBuilder contents = new StringBuilder();
         getCoverageUnitContentsRecurse(contents, unit);
         String file = baseDir + System.getProperty("file.separator") + unit.getName();
         Lib.writeStringToFile(contents.toString(), new File(file));
      } catch (IOException ex) {
         throw new OseeWrappedException(ex);
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.ADD_GREEN);
   }

   private void getReportDetails(ICoverage packageItem, ICoverage importCoverageEditorItem) throws OseeCoreException {

      StringBuilder packageStr = new StringBuilder();
      cleanupDir();
      createBasepath();
      recurseCreateReport(getPkgDir(), packageStr, packageItem);

      StringBuilder importStr = new StringBuilder();
      recurseCreateReport(getIptDir(), importStr, importCoverageEditorItem);

      createLaunchFile(baseDir);
      launchFile(baseDir);
   }

   public static void launchFile(String baseDir) {
      Program.launch(baseDir + System.getProperty("file.separator") + "merge.bat");
   }

   public void createLaunchFile(String baseDir) throws OseeCoreException {
      try {
         File outFile = new File(baseDir + System.getProperty("file.separator") + "merge.bat");
         String contents =
         //  "@ECHO OFF\ncall C:\\UserData\\WinMerge\\WinMergeU C:\\UserData\\Merge\\pkg C:\\UserData\\Merge\\ipt %*\nREM pause";
            "@ECHO OFF\ncall \"" + compareExec + "\" " + getPkgDir() + " " + getIptDir() + " %*\nREM pause";
         Lib.writeStringToFile(contents, outFile);
      } catch (IOException ex) {
         throw new OseeWrappedException(ex);
      }

   }

   private void createBasepath() {
      File dir = new File(baseDir);
      if (!dir.exists()) {
         dir.mkdir();
      }
      dir = new File(getPkgDir());
      if (!dir.exists()) {
         dir.mkdir();
      }
      dir = new File(getIptDir());
      if (!dir.exists()) {
         dir.mkdir();
      }

   }

   private String getPkgDir() {
      return baseDir + System.getProperty("file.separator") + "pkg";
   }

   private String getIptDir() {
      return baseDir + System.getProperty("file.separator") + "ipt";
   }

   private void cleanupDir() {
      File dir = new File(getPkgDir());
      Lib.deleteDir(dir);
      dir = new File(getIptDir());
      Lib.deleteDir(dir);
   }

   public static boolean isFile(ICoverage coverage) {
      return coverage.getName().endsWith(".ada") || coverage.getName().endsWith(".adb") || coverage.getName().endsWith(
         ".c") || coverage.getName().endsWith(".java");
   }

   private String getOrCreateFolder(String baseDir, CoverageUnit unit) {
      String folder = baseDir + System.getProperty("file.separator") + unit.getName();
      File file = new File(folder);
      file.mkdir();
      return folder;
   }

   public void setPackageXViewer(XCoverageMergeViewer packageXViewer, CoveragePackage coveragePackage) {
      this.coveragePackage = coveragePackage;
   }

   public XCoverageMergeViewer getImportXViewer() {
      return importXViewer;
   }

   public void setImportXViewer(XCoverageMergeViewer importXViewer, CoverageImport coverageImport) {
      this.coverageImport = coverageImport;
      this.importXViewer = importXViewer;
   }

   public CoverageImport getCoverageImport() {
      return coverageImport;
   }

   @Override
   public void run() {
      try {
         compareExec = UserManager.getSetting(COVERAGE_COMPARE_EXEC);
         baseDir = UserManager.getSetting(COVERAGE_MERGE_BASEPATH);
         EntryEntryDialog entry =
            new EntryEntryDialog("Coverage Merge BaseDir", "Enter directory to store merge files for external compare",
               "Compare executable", "Merge Results Dir");
         if (Strings.isValid(compareExec)) {
            entry.setEntry(compareExec);
         }
         if (Strings.isValid(baseDir)) {
            entry.setEntry2(baseDir);
         }
         if (entry.open() != 0) {
            return;
         }

         compareExec = entry.getEntry();
         File compareExecFile = new File(compareExec);
         if (!compareExecFile.exists()) {
            AWorkbench.popup("Compare exec doesn't exist");
            return;
         }
         baseDir = entry.getEntry2();
         File newDir = new File(baseDir);
         if (!newDir.exists()) {
            newDir.mkdir();
         }
         if (!newDir.isDirectory()) {
            AWorkbench.popup("Directory is not a directory");
            return;
         }
         if (!baseDir.equals(UserManager.getSetting(COVERAGE_MERGE_BASEPATH))) {
            UserManager.setSetting(COVERAGE_MERGE_BASEPATH, baseDir);
         }
         if (!compareExec.equals(UserManager.getSetting(COVERAGE_COMPARE_EXEC))) {
            UserManager.setSetting(COVERAGE_COMPARE_EXEC, compareExec);
         }
         if (UserManager.getUser().isDirty()) {
            UserManager.getUser().persist("Store Coverage Compare Defaults");
         }

         if (((ISelectedCoverageEditorItem) importXViewer.getXViewer()).getSelectedCoverageEditorItems().size() == 1) {
            ICoverage importCoverageEditorItem =
               ((ISelectedCoverageEditorItem) importXViewer.getXViewer()).getSelectedCoverageEditorItems().iterator().next();
            // If mergeitemgroup, want to link with parent of one of the children items
            if (importCoverageEditorItem instanceof MergeItemGroup) {
               MergeItemGroup mergeItemGroup = (MergeItemGroup) importCoverageEditorItem;
               // if deleted, just show whole report cause nothing to start at
               if (mergeItemGroup.getMergeType() == MergeType.Delete_And_Reorder || mergeItemGroup.getMergeType() == MergeType.Delete) {
                  getReportDetails(coveragePackage, coverageImport);
                  return;
               } else {
                  importCoverageEditorItem = mergeItemGroup.getMergeItems().iterator().next().getParent();
               }
            } else if (importCoverageEditorItem instanceof MergeItem) {
               importCoverageEditorItem = ((MergeItem) importCoverageEditorItem).getImportItem();
            } else {
               AWorkbench.popup("Must select a Merge Item");
               return;
            }
            if (importCoverageEditorItem instanceof CoverageItem) {
               importCoverageEditorItem = importCoverageEditorItem.getParent();
            }
            while (!importCoverageEditorItem.isFolder() && !isFile(importCoverageEditorItem)) {
               importCoverageEditorItem = importCoverageEditorItem.getParent();
            }
            if (importCoverageEditorItem instanceof CoveragePackageBase) {
               getReportDetails(coveragePackage, coverageImport);
               return;
            }
            MatchItem matchItem = MergeManager.getPackageCoverageItem(coveragePackage, importCoverageEditorItem);
            if (matchItem.getPackageItem() != null) {
               getReportDetails(matchItem.getPackageItem(), importCoverageEditorItem);
            } else {
               AWorkbench.popup("Can't find match item");
               return;
            }
         }
         // If nothing selected, diff entire package/import
         else {
            getReportDetails(coveragePackage, coverageImport);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }
}
