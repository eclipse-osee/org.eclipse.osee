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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Roberto E. Escobar
 */
public class NewArtifactImportWizard extends Wizard implements IImportWizard {
   private File importFile;
   private Artifact reuseRootArtifact;
   private ArtifactImportSourcePage mainPage;
   private IStructuredSelection selection;

   public NewArtifactImportWizard() {
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

   @Override
   public boolean performFinish() {
      //      File file = mainPage.getImportFile();
      //      Branch branch = mainPage.getSelectedBranch();
      //      IArtifactImportResolver artifactResolver = null;
      //
      //      try {
      //         Artifact reuseArtifactRoot = mainPage.getReuseArtifactRoot();
      //         ArtifactExtractor extractor = mainPage.getExtractor();
      //         ArtifactType primaryArtifactType = extractor.usesTypeList() ? mainPage.getSelectedType() : null;
      //         ArtifactType secondaryArtifactType = ArtifactTypeManager.getType("Heading");
      //
      //         if (reuseArtifactRoot == null) {
      //            artifactResolver = new NewArtifactImportResolver(primaryArtifactType, secondaryArtifactType);
      //         } else { // only non-null when reuse artifacts is checked
      //            Collection<AttributeType> identifyingAttributes = attributeTypePage.getSelectedAttributeDescriptors();
      //            artifactResolver =
      //                  new RootAndAttributeBasedArtifactResolver(primaryArtifactType, secondaryArtifactType,
      //                        identifyingAttributes, false);
      //         }
      //
      //         Artifact importRoot = mainPage.getImportRoot();
      //         Jobs.runInJob(new ArtifactImportOperation(file, importRoot, extractor, branch, artifactResolver), true);
      //      } catch (OseeCoreException ex) {
      //         OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, "Exception occured during artifact import", ex);
      //         return false;
      //      }
      return true;
   }

   public void init(IWorkbench workbench, IStructuredSelection selection) {
      mainPage = new ArtifactImportSourcePage();
      if (importFile == null && reuseRootArtifact == null) {
         Pair<File, Artifact> items = getSelection(selection);
         importFile = items.getFirst();
         reuseRootArtifact = items.getSecond();
      }
      mainPage.setDefaultDestinationArtifact(reuseRootArtifact);
      mainPage.setDefaultResource(importFile);
   }

   private Pair<File, Artifact> getSelection(IStructuredSelection selection) {
      Artifact selectedArtifact = null;
      File importResource = null;
      if (selection != null && selection.size() == 1) {
         Object firstElement = selection.getFirstElement();
         if (firstElement instanceof IAdaptable) {
            Object resource = ((IAdaptable) firstElement).getAdapter(IResource.class);
            if (resource instanceof IResource) {
               importResource = ((IResource) resource).getLocation().toFile();
            }
         }
         if (firstElement instanceof Artifact) {
            selectedArtifact = (Artifact) firstElement;
         }
      }
      return new Pair<File, Artifact>(importResource, selectedArtifact);
   }

   @Override
   public void addPages() {
      addPage(mainPage);
   }

   //   @Override
   //   public IWizardPage getNextPage(IWizardPage page) {
   //      try {
   //         if (page == mainPage && mainPage.getReuseArtifactRoot() != null) {
   //
   //            ArtifactType rootDescriptor = mainPage.getReuseArtifactRoot().getArtifactType();
   //            ArtifactType importDescriptor = mainPage.getSelectedType();
   //
   //            HashSet<AttributeType> rootAttributes =
   //                  new HashSet<AttributeType>(TypeValidityManager.getAttributeTypesFromArtifactType(rootDescriptor,
   //                        mainPage.getSelectedBranch()));
   //
   //            if (rootDescriptor == importDescriptor) {
   //               attributeTypePage.setDescription("Identifying attributes for " + rootDescriptor.getName() + " artifacts");
   //               attributeTypePage.setDescriptors(rootAttributes);
   //            } else {
   //               HashSet<AttributeType> importAttributes =
   //                     new HashSet<AttributeType>(TypeValidityManager.getAttributeTypesFromArtifactType(importDescriptor,
   //                           mainPage.getSelectedBranch()));
   //
   //               attributeTypePage.setDescription("Identifying attributes common to " + rootDescriptor.getName() + " and " + importDescriptor.getName() + " artifacts");
   //
   //               importAttributes.addAll(rootAttributes);
   //               attributeTypePage.setDescriptors(importAttributes);
   //            }
   //
   //            return attributeTypePage;
   //         }
   //      } catch (OseeCoreException ex) {
   //         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
   //      }
   //      return null;
   //   }

   //   @Override
   //   public IWizardPage getPreviousPage(IWizardPage page) {
   //      if (page == attributeTypePage || mainPage.getReuseArtifactRoot() == null) {
   //         return mainPage;
   //      }
   //
   //      return null;
   //   }
}
