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

import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoverageOption;
import org.eclipse.osee.coverage.model.CoverageOptionManager;
import org.eclipse.osee.coverage.model.CoverageOptionManagerDefault;
import org.eclipse.osee.coverage.store.CoverageOptionManagerStore;
import org.eclipse.osee.coverage.store.CoverageOptionManagerStore.StoreLocation;
import org.eclipse.osee.coverage.store.OseeCoveragePackageStore;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.coverage.util.dialog.CoveragePackageArtifactListDialog;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Donald G. Dunne
 */
public class ConfigureCoverageMethodsAction extends Action {

   public static KeyedImage OSEE_IMAGE = FrameworkImage.GEAR;

   public ConfigureCoverageMethodsAction() {
      super("Configure Coverage Methods");
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(OSEE_IMAGE);
   }

   @Override
   public void run() {
      try {
         if (!CoverageUtil.getBranchFromUser(false)) {
            return;
         }
         CoveragePackageArtifactListDialog dialog =
               new CoveragePackageArtifactListDialog("Open Coverage Package", "Select Coverage Package");
         dialog.setInput(OseeCoveragePackageStore.getCoveragePackageArtifacts(CoverageUtil.getBranch()));
         if (dialog.open() == 0) {
            Artifact coveragePackageArtifact = (Artifact) dialog.getResult()[0];
            OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(coveragePackageArtifact);
            CoverageOptionManagerStore optionsStore = new CoverageOptionManagerStore(packageStore);
            Result isSaveable = optionsStore.isSaveable();
            if (isSaveable.isFalse()) {
               isSaveable.popup();
               return;
            }
            String coverageOptions = null;
            StoreLocation storeLocation = optionsStore.getStoreLocation();
            if (storeLocation == StoreLocation.None) {
               MessageDialog localGlobalDialog =
                     new MessageDialog(Displays.getActiveShell(), "Question", null,
                           "No Custom Coverage Methods Configured, Configure Now?", MessageDialog.WARNING,
                           new String[] {"Save local to Coverage Pacakge", "Save globally for Branch", "Cancel"}, 0);

               int result = localGlobalDialog.open();
               if (result == 0) {
                  storeLocation = StoreLocation.Local;
               } else if (result == 1) {
                  storeLocation = StoreLocation.Global;
               } else {
                  return;
               }
               coverageOptions = CoverageOptionManagerDefault.instance().toXml();
            } else {
               coverageOptions = optionsStore.getCoverageOptions();
            }

            // Recheck save after option has been chosen
            optionsStore = new CoverageOptionManagerStore(packageStore);
            isSaveable = optionsStore.isSaveable();
            if (isSaveable.isFalse()) {
               isSaveable.popup();
               return;
            }

            boolean successOrCancel = false;
            // Keep allowing user to enter options until valid
            while (!successOrCancel) {
               EntryDialog entryDiag = new EntryDialog(getText(), "Edit Configure Options");
               entryDiag.setFillVertically(true);
               entryDiag.setEntry(coverageOptions);
               if (entryDiag.open() == 0) {
                  coverageOptions = entryDiag.getEntry();
                  try {
                     CoverageOptionManager manager = new CoverageOptionManager(coverageOptions);
                     if (manager.get().isEmpty()) {
                        throw new OseeArgumentException("No options specified");
                     }
                     if (manager.get(CoverageOptionManager.Not_Covered.getName()) == null) {
                        throw new OseeArgumentException("Can't remove Not_Covered item");
                     }
                     Set<String> names = new HashSet<String>();
                     for (CoverageOption option : manager.get()) {
                        if (names.contains(option.getName())) {
                           throw new OseeArgumentException(String.format("Multiple options with same name [%s]",
                                 option.getName()));
                        } else {
                           names.add(option.getName());
                        }
                     }
                     optionsStore.store(coverageOptions, storeLocation);
                     successOrCancel = true;
                  } catch (Exception ex) {
                     OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP,
                           "Invalid coverage options\n\n" + ex.getLocalizedMessage(), ex);
                  }
               } else {
                  successOrCancel = true;
               }
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }
}
