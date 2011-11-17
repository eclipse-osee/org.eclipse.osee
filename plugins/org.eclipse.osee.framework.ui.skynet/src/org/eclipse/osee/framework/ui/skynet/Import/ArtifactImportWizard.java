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

import java.util.logging.Level;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.IArtifactImportResolver;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactImportWizard extends Wizard implements IImportWizard {

   private ArtifactImportPage mainPage;
   private final ArtifactImporter importer;

   public ArtifactImportWizard() {
      this(new ArtifactImporter());
   }

   public ArtifactImportWizard(ArtifactImporter importer) {
      this.importer = importer;
      setDialogSettings(Activator.getInstance().getDialogSettings());
      setWindowTitle("OSEE Artifact Import Wizard");
      setNeedsProgressMonitor(true);
      setHelpAvailable(true);
   }

   @Override
   public void init(IWorkbench workbench, IStructuredSelection selection) {
      if (selection != null && !selection.isEmpty()) {
         Object firstElement = selection.getFirstElement();
         importer.setInputResource(firstElement);
      }
   }

   @Override
   public void addPages() {
      mainPage = new ArtifactImportPage(importer);
      addPage(mainPage);
   }

   @Override
   public boolean performFinish() {
      try {
         return importer.startImportJob(mainPage.getDestinationArtifact(), mainPage.isDeleteUnmatchedSelected(),
            mainPage.getCollectedArtifacts(), getResolver());
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return false;
   }

   private IArtifactImportResolver getResolver() {
      return mainPage.getMatchingStrategy().getResolver(mainPage.getArtifactType(),
         mainPage.getNonChangingAttributes(), true, mainPage.isDeleteUnmatchedSelected());
   }
}
