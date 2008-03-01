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
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeDescriptor;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactImportWizard extends Wizard implements IImportWizard {
   private static final String TITLE = "Import artifacts into Define";
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

      Artifact reuseArtifactRoot = mainPage.getReuseArtifactRoot();
      if (reuseArtifactRoot == null) {
         artifactResolver = new NewArtifactImportResolver();
      } else { // only non-null when reuse artifacts is checked
         Collection<DynamicAttributeDescriptor> identifyingAttributes =
               attributeTypePage.getSelectedAttributeDescriptors();
         artifactResolver = new RootAndAttributeBasedArtifactResolver(identifyingAttributes, true);
      }

      ArtifactSubtypeDescriptor mainDescriptor = null;

      if (mainPage.isGeneralDocumentExtractor() || mainPage.isWholeWordExtractor() || mainPage.isWordOutlineExtractor()) {
         mainDescriptor = mainPage.getSelectedType();
      }

      try {
         ArtifactExtractor extractor = getNewArtifactExtractor(mainDescriptor, branch, reuseArtifactRoot != null);
         Artifact importRoot = mainPage.getImportRoot();
         Jobs.startJob(new ArtifactImportJob(file, importRoot, extractor, branch, artifactResolver));
      } catch (SQLException ex) {
         ErrorDialog.openError(getShell(), "Define Import Error", "An error has occured while importing a document.",
               new Status(IStatus.ERROR, "org.eclipse.osee.framework.jdk.core", IStatus.ERROR,
                     "Exception occured during artifact import", ex));
      }
      return true;
   }

   public ArtifactExtractor getNewArtifactExtractor(ArtifactSubtypeDescriptor primaryDescriptor, Branch branch, boolean reuseArtifacts) throws SQLException {
      if (mainPage.isWordOutlineExtractor()) {
         return new WordOutlineExtractor(primaryDescriptor, branch, 0, handlerPage.getSelectedOutlineContentHandler());
      } else if (mainPage.isExcelExtractor()) {
         return new ExcelArtifactExtractor(branch, reuseArtifacts);
      } else if (mainPage.isWholeWordExtractor()) {
         return new WholeWordDocumentExtractor(primaryDescriptor, branch);
      } else if (mainPage.isGeneralDocumentExtractor()) {
         return new NativeDocumentExtractor(primaryDescriptor, branch);
      } else {
         throw new IllegalStateException("None of the expected extractor buttons are selected");
      }
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
      if (page == mainPage && mainPage.getReuseArtifactRoot() != null) {
         try {
            ConfigurationPersistenceManager manager = ConfigurationPersistenceManager.getInstance();
            ArtifactSubtypeDescriptor rootDescriptor = mainPage.getReuseArtifactRoot().getDescriptor();
            ArtifactSubtypeDescriptor importDescriptor = mainPage.getSelectedType();

            HashSet<DynamicAttributeDescriptor> rootAttributes =
                  new HashSet<DynamicAttributeDescriptor>(manager.getAttributeTypesFromArtifactType(rootDescriptor,
                        mainPage.getSelectedBranch()));

            if (rootDescriptor == importDescriptor) {
               attributeTypePage.setDescription("Identifying attributes for " + rootDescriptor.getName() + " artifacts");
               attributeTypePage.setDescriptors(rootAttributes);
            } else {
               HashSet<DynamicAttributeDescriptor> importAttributes =
                     new HashSet<DynamicAttributeDescriptor>(manager.getAttributeTypesFromArtifactType(
                           importDescriptor, mainPage.getSelectedBranch()));

               attributeTypePage.setDescription("Identifying attributes common to " + rootDescriptor.getName() + " and " + importDescriptor.getName() + " artifacts");

               importAttributes.addAll(rootAttributes);
               attributeTypePage.setDescriptors(importAttributes);
            }
         } catch (SQLException ex) {
            SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
         }
         return attributeTypePage;
      } else if (mainPage.isWordOutlineExtractor() && page != handlerPage) {
         return handlerPage;
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
      return mainPage.isPageComplete() && (mainPage.getReuseArtifactRoot() == null || attributeTypePage.isPageComplete()) && (!mainPage.isWordOutlineExtractor() || handlerPage.isPageComplete());
   }
}
