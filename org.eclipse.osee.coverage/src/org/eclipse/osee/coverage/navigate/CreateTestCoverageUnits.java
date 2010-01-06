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
package org.eclipse.osee.coverage.navigate;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageOptionManager;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.coverage.store.OseeCoveragePackageStore;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.coverage.util.dialog.CoveragePackageArtifactListDialog;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class CreateTestCoverageUnits extends XNavigateItemAction {

   public CreateTestCoverageUnits() {
      super(null, "Create Test CoverageUnits", FrameworkImage.ADMIN);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws OseeCoreException {
      if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), getName(),
            getName() + "\n\nThis will create test CoverageUnits to an existing Coverage Package.")) {
         return;
      }

      try {
         if (!CoverageUtil.getBranchFromUser(false)) return;
         CoveragePackageArtifactListDialog dialog =
               new CoveragePackageArtifactListDialog(getName(), "Select Coverage Package");
         dialog.setInput(OseeCoveragePackageStore.getCoveragePackageArtifacts());
         if (dialog.open() == 0) {
            Artifact coveragePackageArtifact = (Artifact) dialog.getResult()[0];
            OseeCoveragePackageStore store = new OseeCoveragePackageStore(coveragePackageArtifact);
            CoveragePackage coveragePackage = store.getCoveragePackage();
            for (ICoverage coverage : coveragePackage.getChildren()) {
               if (coverage.getName().equals("test")) {
                  throw new OseeStateException("test Coverage Folder already exists; can't create");
               }
            }
            CoverageUnit topCoverageUnit = new CoverageUnit(coveragePackage, "test", "C:/This is a test", null);
            topCoverageUnit.setNamespace("test");
            topCoverageUnit.setFolder(true);

            CoverageUnit test1CU = new CoverageUnit(topCoverageUnit, "test1.ada", "C:\\UserData\\", null);
            test1CU.setOrderNumber("1");
            test1CU.setNamespace("test");
            CoverageItem item = new CoverageItem(test1CU, CoverageOptionManager.Exception_Handling, "1");
            item.setName("   System.out.println(\"this is a test\");");
            item = new CoverageItem(test1CU, CoverageOptionManager.Test_Unit, "2");
            item.setName("   System.out.println(\"this is a another test\");");
            item = new CoverageItem(test1CU, CoverageOptionManager.Not_Covered, "3");
            item.setName("   System.out.println(\"this is a third test\");");

            CoverageUnit test2CU = new CoverageUnit(topCoverageUnit, "test2.ada", "C:\\UserData\\", null);
            test2CU.setOrderNumber("2");
            test2CU.setNamespace("test");
            item = new CoverageItem(test2CU, CoverageOptionManager.Exception_Handling, "1");
            item.setName("   System.out.println(\"this is just a test\");");

            store.save();
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      AWorkbench.popup("Completed", "Complete");
   }
}
