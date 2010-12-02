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
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughArtifactCollector;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.IArtifactImportResolver;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactImportWizard extends Wizard implements IImportWizard {
   private File importResource;
   private Artifact defaultDestinationArtifact;
   private ArtifactImportPage mainPage;

   public ArtifactImportWizard() {
      super();
      setDialogSettings(SkynetGuiPlugin.getInstance().getDialogSettings());
      setWindowTitle("OSEE Artifact Import Wizard");
      setNeedsProgressMonitor(true);
      setHelpAvailable(true);
   }

   public void setImportResourceAndArtifactDestination(File importResource, Artifact defaultDestinationArtifact) {
      Assert.isNotNull(importResource);
      Assert.isNotNull(defaultDestinationArtifact);

      this.importResource = importResource;
      this.defaultDestinationArtifact = defaultDestinationArtifact;
   }

   @Override
   public void init(IWorkbench workbench, IStructuredSelection selection) {
      if (importResource == null && defaultDestinationArtifact == null) {
         if (selection != null && selection.size() == 1) {
            Object firstElement = selection.getFirstElement();
            if (firstElement instanceof IAdaptable) {
               Object resource = ((IAdaptable) firstElement).getAdapter(IResource.class);
               if (resource instanceof IResource) {
                  importResource = ((IResource) resource).getLocation().toFile();
               }
            }
            if (firstElement instanceof Artifact) {
               defaultDestinationArtifact = (Artifact) firstElement;
            }
         }
      }
   }

   @Override
   public void addPages() {
      mainPage = new ArtifactImportPage();
      mainPage.setDefaultDestinationArtifact(defaultDestinationArtifact);
      mainPage.setDefaultSourceFile(importResource);
      addPage(mainPage);
   }

   @Override
   public boolean performFinish() {
      final Artifact destinationArtifact = mainPage.getDestinationArtifact();
      final boolean isDeleteUnmatchedSelected = mainPage.isDeleteUnmatchedSelected();
      final RoughArtifactCollector roughItems = mainPage.getCollectedArtifacts();
      final IArtifactImportResolver resolver = getResolver();

      final String opName = String.format("Importing Artifacts onto: [%s]", destinationArtifact);
      IOperation operation =
         ArtifactImportOperationFactory.createRoughToRealOperation(opName, destinationArtifact, resolver, false,
            roughItems, isDeleteUnmatchedSelected);
      Job job = Operations.executeAsJob(operation, true);
      return job != null;
   }

   private IArtifactImportResolver getResolver() {
      MatchingStrategy strategy = mainPage.getMatchingStrategy();
      return strategy.getResolver(mainPage.getArtifactType(), mainPage.getNonChangingAttributes(), true,
         mainPage.isDeleteUnmatchedSelected());
   }
}
