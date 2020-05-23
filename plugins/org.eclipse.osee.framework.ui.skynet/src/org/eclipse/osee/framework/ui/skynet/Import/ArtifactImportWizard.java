/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.Import;

import java.io.File;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughArtifactCollector;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.IArtifactImportResolver;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactResolverFactory.ArtifactCreationStrategy;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactImportWizard extends Wizard implements IImportWizard {

   private ArtifactImportPage mainPage;

   private File defaultSourceFile;
   private Artifact defaultDestinationArtifact;

   public ArtifactImportWizard() {
      setDialogSettings(Activator.getInstance().getDialogSettings());
      setWindowTitle("OSEE Artifact Import Wizard");
      setNeedsProgressMonitor(true);
      setHelpAvailable(true);
   }

   @Override
   public void init(IWorkbench workbench, IStructuredSelection selection) {
      if (selection != null && !selection.isEmpty()) {
         Object firstElement = selection.getFirstElement();
         if (firstElement instanceof IAdaptable) {
            Object resource = ((IAdaptable) firstElement).getAdapter(IResource.class);
            if (resource instanceof IResource) {
               setImportFile(((IResource) resource).getLocation().toFile());
            }
         }
         if (firstElement instanceof Artifact) {
            setDestinationArtifact((Artifact) firstElement);
         }
      }
   }

   public void setImportFile(File importFile) {
      this.defaultSourceFile = importFile;
   }

   public void setDestinationArtifact(Artifact destinationArtifact) {
      this.defaultDestinationArtifact = destinationArtifact;
   }

   @Override
   public void addPages() {
      mainPage = new ArtifactImportPage(defaultSourceFile, defaultDestinationArtifact);
      addPage(mainPage);
   }

   @Override
   public boolean performFinish() {
      boolean wasLaunched = false;
      try {
         File importResource = mainPage.getSourceFile();
         Artifact destinationArtifact = mainPage.getDestinationArtifact();

         if (importResource == null) {
            importResource = defaultSourceFile;
         }
         if (destinationArtifact == null) {
            destinationArtifact = defaultDestinationArtifact;
         }

         boolean isDeleteUnmatchedSelected = mainPage.isDeleteUnmatchedSelected();
         RoughArtifactCollector roughItems = mainPage.getCollectedArtifacts();
         IArtifactImportResolver resolver = getResolver();

         Conditions.checkNotNull(importResource, "importResource");
         Conditions.checkNotNull(destinationArtifact, "destinationArtifact");

         final String opName = String.format("Importing Artifacts onto: [%s]", destinationArtifact);

         IOperation operation = ArtifactImportOperationFactory.createRoughToRealOperation(opName, destinationArtifact,
            resolver, false, roughItems, isDeleteUnmatchedSelected, mainPage.getArtifactParser());
         Operations.executeAsJob(operation, true);
         wasLaunched = true;

         defaultDestinationArtifact = destinationArtifact;
         defaultSourceFile = importResource;

      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return wasLaunched;
   }

   private IArtifactImportResolver getResolver() {
      ArtifactCreationStrategy strategy = mainPage.getArtifactCreationStrategy();
      return ArtifactResolverFactory.createResolver(strategy, mainPage.getArtifactType(),
         mainPage.getNonChangingAttributes(), true, mainPage.isDeleteUnmatchedSelected());
   }
}
