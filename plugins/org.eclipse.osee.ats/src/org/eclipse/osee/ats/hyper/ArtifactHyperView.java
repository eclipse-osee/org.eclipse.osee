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
package org.eclipse.osee.ats.hyper;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.actions.wizard.ArtifactSelectWizard;
import org.eclipse.osee.ats.editor.SMAEditor;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.IActionable;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.ui.plugin.OseeUiActions;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class ArtifactHyperView extends HyperView implements IArtifactEventListener, IActionable, IPerspectiveListener2 {

   public static String VIEW_ID = "org.eclipse.osee.ats.hyper.ArtifactHyperView";
   public ArtifactHyperItem topAHI;
   public Artifact currentArtifact;

   public ArtifactHyperView() {
      super();
      PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener(this);
      PlatformUI.getWorkbench().getActiveWorkbenchWindow().addPerspectiveListener(this);
      HyperView.setNodeColor(whiteColor);
      HyperView.setCenterColor(whiteColor);
      defaultZoom.pcRadius += defaultZoom.pcRadiusFactor * 2;
      defaultZoom.uuRadius += defaultZoom.uuRadiusFactor * 2;
      defaultZoom.xSeparation += defaultZoom.xSeparationFactor;
   }

   public static ArtifactHyperView getArtifactHyperView() {
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      try {
         return (ArtifactHyperView) page.showView(ArtifactHyperView.VIEW_ID);
      } catch (PartInitException e1) {
         MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Launch Error",
            "Couldn't Get OSEE Artifact Hyper View " + e1.getMessage());
      }
      return null;
   }

   public static void openArtifact(Artifact artifact) {
      getArtifactHyperView().load(artifact);
   }

   @Override
   public void createPartControl(Composite top) {
      if (!DbConnectionExceptionComposite.dbConnectionIsOk(top)) {
         PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().removePartListener(this);
         PlatformUI.getWorkbench().getActiveWorkbenchWindow().removePerspectiveListener(this);
         return;
      }
      super.createPartControl(top);
      OseeUiActions.addBugToViewToolbar(this, this, AtsPlugin.PLUGIN_ID, VIEW_ID, "ATS Action View");
      OseeEventManager.addListener(this);
   }

   @Override
   public void handleItemDoubleClick(HyperViewItem hvi) {
      if (hvi instanceof ArtifactHyperItem) {
         currentArtifact = ((ArtifactHyperItem) hvi).getArtifact();
      }
      display();
   }

   private void load(Artifact artifact) {
      currentArtifact = artifact;
      display();
   }

   @Override
   public void display() {
      try {
         if (currentArtifact == null) {
            return;
         }
         topAHI = new ArtifactHyperItem(currentArtifact);
         // System.out.println("Artifact "+currentArtifact.getArtifactTypeNameShort());
         int x = 0;
         for (RelationLink link : currentArtifact.getRelationsAll(false)) {

            // Don't process link if onlyShowRel is populated and doesn't contain link name
            if (onlyShowRelations.size() > 0) {
               if (!onlyShowRelations.contains(link.getRelationType().getName())) {
                  continue;
               }
               x++;
               if (x == 4) {
                  x = 0;
               }
            }

            Artifact otherArt = link.getArtifactB();
            if (otherArt.equals(currentArtifact)) {
               otherArt = link.getArtifactA();
            }
            if (!otherArt.isDeleted()) {
               ArtifactHyperItem ahi = new ArtifactHyperItem(otherArt);
               String tip = link.getRelationType().getName();
               if (!link.getRationale().equals("")) {
                  tip += "(" + link.getRationale() + ")";
               }
               ahi.setRelationToolTip(tip);
               String label = "";
               if (!link.getRationale().equals("")) {
                  label += "(" + link.getRationale() + ")";
               }
               ahi.setRelationLabel(label);
               ahi.setLink(link);
               ahi.setRelationDirty(link.isDirty());
               switch (x) {
                  case 0:
                     topAHI.addBottom(ahi);
                     break;
                  case 1:
                     topAHI.addLeft(ahi);
                     break;
                  case 2:
                     topAHI.addTop(ahi);
                     break;
                  case 3:
                     topAHI.addRight(ahi);
                     break;
                  default:
                     break;
               }
            }
            x++;
            if (x == 4) {
               x = 0;
            }
         }
         create(topAHI);
         center();
      } catch (OseeCoreException ex) {
         clear();
      }
   }

   @Override
   protected void handleRefreshButton() {
      display();
   }

   @Override
   public void dispose() {
      OseeEventManager.removeListener(this);
      super.dispose();
   }

   public void handleWindowChange() {
      if (!this.getSite().getPage().isPartVisible(this)) {
         return;
      }
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      if (page != null) {
         IEditorPart editor = page.getActiveEditor();
         if (editor != null && editor instanceof SMAEditor) {
            currentArtifact = ((SMAEditor) editor).getSma();
            load(currentArtifact);
         }
      }
   }

   @Override
   public void partActivated(IWorkbenchPart part) {
      handleWindowChange();
   }

   @Override
   public void partBroughtToTop(IWorkbenchPart part) {
      handleWindowChange();
   }

   @Override
   public void partClosed(IWorkbenchPart part) {
      if (part.equals(this)) {
         dispose();
      } else {
         handleWindowChange();
      }
   }

   @Override
   public void partDeactivated(IWorkbenchPart part) {
      handleWindowChange();
   }

   @Override
   public void partOpened(IWorkbenchPart part) {
      handleWindowChange();
   }

   @Override
   protected void createActions() {
      super.createActions();

      Action openArtAction = new Action("Open Artifact") {

         @Override
         public void run() {
            handleOpenArtifact();
         }
      };
      openArtAction.setToolTipText("Open Artifact");
      openArtAction.setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.ART_VIEW));

      Action openByIdAction = new Action("Open by Id", IAction.AS_PUSH_BUTTON) {

         @Override
         public void run() {
            EntryDialog ed = new EntryDialog("Open by Id", "Enter HRID/Guid for Artifact");
            if (ed.open() != 0) {
               return;
            }
            String hridGuid = ed.getEntry().trim();
            try {
               Collection<Artifact> arts =
                  ArtifactQuery.getArtifactListFromIds(Arrays.asList(hridGuid), BranchManager.getCommonBranch());
               if (arts.isEmpty()) {
                  AWorkbench.popup("ERROR", "No Artifacts Found");
                  return;
               }
               load(arts.iterator().next());
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            }
         }
      };
      openByIdAction.setToolTipText("Open by Id");
      openArtAction.setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.ART_VIEW));

      IActionBars bars = getViewSite().getActionBars();
      IToolBarManager tbm = bars.getToolBarManager();
      tbm.add(new Separator());
      tbm.add(openByIdAction);
      tbm.add(openArtAction);
   }

   public void handleOpenArtifact() {

      ArtifactSelectWizard selWizard = new ArtifactSelectWizard();
      WizardDialog dialog =
         new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), selWizard);
      dialog.create();
      if (dialog.open() == 0) {
         load(selWizard.getSelectedArtifact());
      }
   }

   @Override
   public String getActionDescription() {
      if (currentArtifact != null && currentArtifact.isDeleted()) {
         return String.format("Current Artifact - %s - %s", currentArtifact.getGuid(), currentArtifact.getName());
      }
      return "";
   }

   @Override
   public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
      handleWindowChange();
   }

   @Override
   public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId) {
      handleWindowChange();
   }

   @Override
   public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, IWorkbenchPartReference partRef, String changeId) {
      handleWindowChange();
   }

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      return null;
   }

   @Override
   public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
      if (sender.isRemote()) {
         return;
      }
      if (currentArtifact == null) {
         return;
      }
      if (!artifactEvent.isForBranch(currentArtifact.getBranch())) {
         return;
      }

      if (artifactEvent.isDeletedPurged(currentArtifact)) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               clear();
            }
         });
      }
      if (artifactEvent.isRelAddedChangedDeleted(currentArtifact)) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               display();
            }
         });
      }

   }
}
