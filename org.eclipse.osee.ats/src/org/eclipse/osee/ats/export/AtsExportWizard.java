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

package org.eclipse.osee.ats.export;

import java.util.Collection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.export.AtsExportManager.ExportOption;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Donald G. Dunne
 */
public class AtsExportWizard extends Wizard implements IExportWizard {
   private AtsExportPage mainPage;
   private final Collection<? extends Artifact> artifacts;

   public AtsExportWizard() {
      this.artifacts = null;
   }

   public AtsExportWizard(Collection<? extends Artifact> artifacts) {
      this.artifacts = artifacts;
   }

   /**
    * @return the fileLocation
    */
   public String getFileLocation() {
      return mainPage.getFileLocation();
   }

   /**
    * @return the selectedExportOptions
    */
   public Collection<ExportOption> getSelectedExportOptions() {
      return mainPage.getSelectedExportOptions();
   }

   @Override
   public boolean performFinish() {

      Result result = mainPage.isEntryValid();
      if (result.isFalse()) {
         result.popup();
         return false;
      }
      try {
         AtsExportManager.export(artifacts, mainPage.getSelectedExportOptions().toArray(
               new ExportOption[mainPage.getSelectedExportOptions().size()]));
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return true;
   }

   @Override
   public void init(IWorkbench workbench, IStructuredSelection selection) {
      try {
         mainPage = new AtsExportPage(selection);
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public void addPages() {
      if (mainPage == null) {
         mainPage = new AtsExportPage(artifacts);
      }
      addPage(mainPage);
   }
}