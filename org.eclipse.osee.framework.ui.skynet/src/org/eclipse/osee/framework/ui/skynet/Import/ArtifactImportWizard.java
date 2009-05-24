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
import java.util.Collection;
import java.util.HashSet;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.TypeValidityManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactImportWizard extends Wizard implements IImportWizard {
   private static final String TITLE = "Import artifacts into OSEE";
   private ArtifactImportPage mainPage;
   private AttributeTypePage attributeTypePage;
   private OutlineContentHandlerPage handlerPage;
   private File importFile;
   private Artifact reuseRootArtifact;

   public ArtifactImportWizard() {
      super();
      setDialogSettings(SkynetGuiPlugin.getInstance().getDialogSettings());
      setWindowTitle("Artifact Import Wizard");
   }

   public void setImportResourceAndArtifactDestination(File importFile, Artifact reuseRootArtifact) {
      Assert.isNotNull(importFile);
      Assert.isNotNull(reuseRootArtifact);

      this.importFile = importFile;
      this.reuseRootArtifact = reuseRootArtifact;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.wizard.Wizard#performFinish()
    */
   @Override
   public boolean performFinish() {
      File file = mainPage.getImportFile();
      Branch branch = mainPage.getSelectedBranch();
      IArtifactImportResolver artifactResolver = null;

      try {
         Artifact reuseArtifactRoot = mainPage.getReuseArtifactRoot();
         ArtifactExtractor extractor = mainPage.getExtractor();
         ArtifactType primaryArtifactType = extractor.usesTypeList() ? mainPage.getSelectedType() : null;
         ArtifactType secondaryArtifactType = ArtifactTypeManager.getType("Heading");

         if (reuseArtifactRoot == null) {
            artifactResolver = new NewArtifactImportResolver(primaryArtifactType, secondaryArtifactType);
         } else { // only non-null when reuse artifacts is checked
            Collection<AttributeType> identifyingAttributes = attributeTypePage.getSelectedAttributeDescriptors();
            artifactResolver =
                  new RootAndAttributeBasedArtifactResolver(primaryArtifactType, secondaryArtifactType,
                        identifyingAttributes, false);
         }

         Artifact importRoot = mainPage.getImportRoot();
         Jobs.runInJob(new ArtifactImportOperation(file, importRoot, extractor, branch, artifactResolver), true);
      } catch (OseeCoreException ex) {
         OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, "Exception occured during artifact import", ex);
         return false;
      }
      return true;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
    *      org.eclipse.jface.viewers.IStructuredSelection)
    */
   public void init(IWorkbench workbench, IStructuredSelection selection) {
      if (importFile != null && reuseRootArtifact != null) {
         this.mainPage = new ArtifactImportPage(importFile, reuseRootArtifact);
      } else {
         this.mainPage = new ArtifactImportPage(selection);
      }
      this.attributeTypePage = new AttributeTypePage();
      this.handlerPage = new OutlineContentHandlerPage();

      mainPage.setTitle(TITLE);
      mainPage.setDescription("Import artifacts into Define");
      attributeTypePage.setTitle(TITLE);
      handlerPage.setTitle(TITLE);
      handlerPage.setDescription("Handler to use for getting Artifacts from the outline");
   }

   /**
    * (non-Javadoc) Method declared on Wizard.
    */
   public void addPages() {
      addPage(mainPage);
      addPage(attributeTypePage);
      addPage(handlerPage);
   }

   @Override
   public IWizardPage getNextPage(IWizardPage page) {
      try {
         if (page == mainPage && mainPage.getReuseArtifactRoot() != null) {

            ArtifactType rootDescriptor = mainPage.getReuseArtifactRoot().getArtifactType();
            ArtifactType importDescriptor = mainPage.getSelectedType();

            HashSet<AttributeType> rootAttributes =
                  new HashSet<AttributeType>(TypeValidityManager.getAttributeTypesFromArtifactType(rootDescriptor,
                        mainPage.getSelectedBranch()));

            if (rootDescriptor == importDescriptor) {
               attributeTypePage.setDescription("Identifying attributes for " + rootDescriptor.getName() + " artifacts");
               attributeTypePage.setDescriptors(rootAttributes);
            } else {
               HashSet<AttributeType> importAttributes =
                     new HashSet<AttributeType>(TypeValidityManager.getAttributeTypesFromArtifactType(importDescriptor,
                           mainPage.getSelectedBranch()));

               attributeTypePage.setDescription("Identifying attributes common to " + rootDescriptor.getName() + " and " + importDescriptor.getName() + " artifacts");

               importAttributes.addAll(rootAttributes);
               attributeTypePage.setDescriptors(importAttributes);
            }

            return attributeTypePage;
         } else if (mainPage.needsSecondaryPage() && page != handlerPage) {
            return handlerPage;
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return null;
   }

   @Override
   public IWizardPage getPreviousPage(IWizardPage page) {
      if (page == attributeTypePage || (page == handlerPage && mainPage.getReuseArtifactRoot() == null)) {
         return mainPage;
      } else if (page == handlerPage) {
         return attributeTypePage;
      }

      return null;
   }

   @Override
   public boolean canFinish() {
      try {
         return mainPage.isPageComplete() && (mainPage.getReuseArtifactRoot() == null || attributeTypePage.isPageComplete()) && (!mainPage.needsSecondaryPage() || handlerPage.isPageComplete());
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         return false;
      }
   }
}
