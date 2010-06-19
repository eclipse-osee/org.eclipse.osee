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
import java.util.Collection;
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
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.CompositeOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.importing.operations.CompleteArtifactImportOperation;
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
      final String opName = "Importing Artifacts onto: " + destinationArtifact;
      final RoughArtifactCollector roughItems = mainPage.getCollectedArtifacts();
      final IArtifactImportResolver resolver = getResolver();

      Operations.executeAsJob(new AbstractOperation(opName, SkynetGuiPlugin.PLUGIN_ID) {
         @Override
         protected void doWork(IProgressMonitor monitor) throws Exception {
            SkynetTransaction transaction = null;
            transaction = new SkynetTransaction(destinationArtifact.getBranch(), "Artifact Import Wizard transaction");
            List<Artifact> children = new ArrayList<Artifact>();
            try {
               children = destinationArtifact.getDescendants();
            } catch (OseeCoreException ex) {
               reportError("Unable to get artifact children: artifact:[%s] branch:[%s]", destinationArtifact.getGuid(),
                     destinationArtifact.getBranch().getGuid(), ex);
            }
            List<IOperation> subOps = new ArrayList<IOperation>();
            subOps.add(new RoughToRealArtifactOperation(transaction, destinationArtifact, roughItems, resolver,
                  isDeleteUnmatchedSelected));
            subOps.add(new ArtifactValidationCheckOperation(children, false));
            subOps.add(new CompleteArtifactImportOperation(transaction, destinationArtifact));
            IOperation ret = new CompositeOperation(opName, SkynetGuiPlugin.PLUGIN_ID, subOps);
            Operations.executeWorkAndCheckStatus(ret, monitor, -1);
         }

         private void reportError(String message, String arg1, String arg2, Exception ex) throws Exception {
            throw new Exception(String.format(message, arg1, arg2), ex);
         }

      }, true);
      return true;
   }

   private IArtifactImportResolver getResolver() {
      ArtifactType primaryArtifactType = mainPage.getArtifactType();
      Collection<AttributeType> nonChangingAttributes = mainPage.getNonChangingAttributes();
      MatchingStrategy strategy = mainPage.getMatchingStrategy();
      try {
         IArtifactImportResolver resolver =
               strategy.getResolver(primaryArtifactType, nonChangingAttributes, true,
                     mainPage.isDeleteUnmatchedSelected());
         return resolver;
      } catch (OseeCoreException ex) {
         String msg = "getResolver() could not retrieve artifact type \"Heading\"";
         ErrorDialog.openError(getContainer().getShell(), "Artifact Import", null, new Status(IStatus.ERROR,
               SkynetGuiPlugin.PLUGIN_ID, msg, ex));
         return null;
      }
   }
}
