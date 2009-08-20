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
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.CompositeOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughArtifactCollector;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughToRealArtifactOperation;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.IArtifactImportResolver;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.ArtifactValidationCheckOperation;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Roberto E. Escobar
 */
public class NewArtifactImportWizard extends Wizard implements IImportWizard {
   private File importResource;
   private Artifact defaultDestinationArtifact;
   private ArtifactImportSourcePage mainPage;

   public NewArtifactImportWizard() {
      super();
      setDialogSettings(SkynetGuiPlugin.getInstance().getDialogSettings());
      setWindowTitle("Artifact Import Wizard");
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
      mainPage = new ArtifactImportSourcePage();
      mainPage.setDefaultDestinationArtifact(defaultDestinationArtifact);
      mainPage.setDefaultSourceFile(importResource);
      addPage(mainPage);
   }

   @Override
   public boolean performFinish() {

      //  mainPage.isReUseSelected();
      //  mainPage.getResolver();
      //      try {
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

      Artifact destinationArtifact = mainPage.getDestinationArtifact();
      String opName = "Importing Artifacts onto: " + destinationArtifact;

      SkynetTransaction transaction = null;
      try {
         transaction = new SkynetTransaction(destinationArtifact.getBranch());
      } catch (OseeCoreException ex) {
         String msg =
               String.format("Unable to create transaction for: artifact:[%s] branch:[%s]",
                     destinationArtifact.getGuid(), destinationArtifact.getBranch().getGuid());
         ErrorDialog.openError(getContainer().getShell(), opName, null, new Status(IStatus.ERROR,
               SkynetGuiPlugin.PLUGIN_ID, msg, ex));
      }

      if (transaction != null) {
         RoughArtifactCollector roughItems = mainPage.getCollectedArtifacts();
         IArtifactImportResolver resolver = null;

         List<IOperation> subOps = new ArrayList<IOperation>();
         subOps.add(new RoughToRealArtifactOperation(transaction, destinationArtifact, roughItems, resolver));
         subOps.add(new CompleteImportOperation(transaction, destinationArtifact));
         Operations.executeAsJob(new CompositeOperation(opName, SkynetGuiPlugin.PLUGIN_ID, subOps), true);
      }
      return true;
   }
   private final class CompleteImportOperation extends AbstractOperation {
      private final Artifact destinationArtifact;
      private final SkynetTransaction transaction;

      public CompleteImportOperation(SkynetTransaction transaction, Artifact destinationArtifact) {
         super("Commit & Verify import", SkynetGuiPlugin.PLUGIN_ID);
         this.destinationArtifact = destinationArtifact;
         this.transaction = transaction;
      }

      @Override
      protected void doWork(IProgressMonitor monitor) throws Exception {
         monitor.setTaskName("Validate artifacts");
         IOperation subOperation = new ArtifactValidationCheckOperation(destinationArtifact.getDescendants(), false);
         doSubWork(subOperation, monitor, 0.50);

         monitor.setTaskName("Commit transaction");
         destinationArtifact.persistAttributesAndRelations(transaction);
         transaction.execute();
         monitor.worked(calculateWork(0.50));
      }
   }
}
