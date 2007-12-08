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
package org.eclipse.osee.framework.ui.skynet.commandHandlers;

import java.util.List;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.HandlerEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.revision.ArtifactChange;
import org.eclipse.osee.framework.skynet.core.revision.ChangeReportInput;
import org.eclipse.osee.framework.skynet.core.revision.TransactionData;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.changeReport.ChangeReportView;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;

/**
 * @author Paul K. Waldfogel
 */
public abstract class AbstractSelectionHandler extends AbstractHandler {
   private boolean transactionDataNeeded = false;
   private boolean changeTableTreeViewerNeeded = false;
   // private final boolean artifactIDsNeeded = false;
   // private boolean artifactChangeGetArtifactGetBranchNeeded = false;
   private List<Artifact> mySelectedArtifactList = null;
   private List<Integer> mySelectedArtifactIDList = null;
   private List<ArtifactChange> mySelectedArtifactChangeList = null;
   private List<Branch> mySelectedBranchList = null;
   private List<TransactionData> mySelectedTransactionDataList = null;
   private List<ChangeReportInput> myChangeReportInputNewList = null;
   private List<ChangeReportInput> myChangeReportInputOldList = null;

   private final HandlerEvent enabledChangedEvent = new HandlerEvent(this, true, false);
   private IStructuredSelection myIStructuredSelection = null;
   private final IWorkbenchPartSite myIWorkbenchPartSite = null;
   private TreeViewer myChangeTableTreeViewer = null;

   public AbstractSelectionHandler(String[] mySelectionTypes) {
      for (String mySelectionTypeString : mySelectionTypes) {
         if (mySelectionTypeString.matches("TransactionData")) transactionDataNeeded = true;
         if (mySelectionTypeString.matches("ChangeTableTreeViewer")) changeTableTreeViewerNeeded = true;
      }
      IWorkbenchPart myIWorkbenchPart = AWorkbench.getActivePage().getActivePart();
      IWorkbenchPartSite myIWorkbenchPartSite = myIWorkbenchPart.getSite();
      if (changeTableTreeViewerNeeded) {
         if (myIWorkbenchPart instanceof ChangeReportView) {
            ChangeReportView myChangeReportView = (ChangeReportView) myIWorkbenchPart;
            myChangeTableTreeViewer = myChangeReportView.getChangeTableTreeViewer();
         }
      }
      ISelectionProvider myISelectionProvider = myIWorkbenchPartSite.getSelectionProvider();
      myISelectionProvider.addSelectionChangedListener(new ISelectionChangedListener() {
         /*
          * (non-Javadoc)
          * 
          * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
          */
         public void selectionChanged(SelectionChangedEvent event) {
            getNeededTypes();
            fireHandlerChanged(enabledChangedEvent);

         }

      });
      getNeededTypes();
   }

   /**
    * 
    */
   @SuppressWarnings("unchecked")
   private void getNeededTypes() {
      myIStructuredSelection = (IStructuredSelection) AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider().getSelection();
      List<Object> myObjects = Handlers.getBranchesAndArtifactsFromStructuredSelection(myIStructuredSelection);
      mySelectedBranchList = (List<Branch>) myObjects.get(0);
      mySelectedArtifactList = (List<Artifact>) myObjects.get(1);
      mySelectedArtifactIDList = (List<Integer>) myObjects.get(2);
      mySelectedArtifactChangeList = (List<ArtifactChange>) myObjects.get(3);
      myChangeReportInputNewList = (List<ChangeReportInput>) myObjects.get(4);
      myChangeReportInputOldList = (List<ChangeReportInput>) myObjects.get(5);

      if (transactionDataNeeded) mySelectedTransactionDataList = Handlers.getTransactionDataNeededFromStructuredSelection(myIStructuredSelection);
   }

   /**
    * @return Returns a ChangeReportInputNewList acquired from the active page.
    */
   protected List<ChangeReportInput> getChangeReportInputNewList() {
      return myChangeReportInputNewList;
   }

   /**
    * @return Returns a ChangeReportInputOldList acquired from the active page.
    */
   protected List<ChangeReportInput> getChangeReportInputOldList() {
      return myChangeReportInputOldList;
   }

   /**
    * @return Returns a ChangeTableTreeViewer acquired from the active page.
    */
   protected TreeViewer getChangeTableTreeViewer() {
      return myChangeTableTreeViewer;
   }

   /**
    * @return Returns a IStructuredSelection acquired from the active page.
    */
   protected IStructuredSelection getIStructuredSelection() {
      return myIStructuredSelection;
   }

   /**
    * @return Returns a IWorkbenchPartSite acquired from the active page.
    */
   protected IWorkbenchPartSite getIWorkbenchPartSite() {
      return myIWorkbenchPartSite;
   }

   /**
    * @return Returns a list of artifacts acquired from an IStructuredSelection from the active page.
    */
   protected List<Artifact> getArtifactList() {
      return mySelectedArtifactList;
   }

   /**
    * @return Returns a list of Branches acquired from an IStructuredSelection from the active page.
    */
   protected List<Branch> getBranchList() {
      return mySelectedBranchList;
   }

   /**
    * @return Returns a list of TransactionData acquired from an IStructuredSelection from the active page.
    */
   protected List<TransactionData> getTransactionDataList() {
      return mySelectedTransactionDataList;
   }

   /**
    * @return Returns a list of ArtifactChange acquired from an IStructuredSelection from the active page.
    */
   protected List<ArtifactChange> getArtifactChangeList() {
      return mySelectedArtifactChangeList;
   }

   /**
    * @return Returns a list of ArtifactChangeIDs acquired from an IStructuredSelection from the active page.
    */
   protected List<Integer> getArtifactIDList() {
      return mySelectedArtifactIDList;
   }

   /**
    * This method should be overridden by a subclass that requires access control.
    * 
    * @return Returns the permission level to be used for access control
    */
   protected PermissionEnum permissionLevel() {
      return null;
   }

}
