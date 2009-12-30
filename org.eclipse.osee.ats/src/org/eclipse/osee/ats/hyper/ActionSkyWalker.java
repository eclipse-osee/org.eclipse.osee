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

import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSArtifact;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.editor.SMAEditor;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData;
import org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.skywalker.SkyWalkerOptions;
import org.eclipse.osee.framework.ui.skynet.skywalker.SkyWalkerView;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class ActionSkyWalker extends SkyWalkerView implements IPartListener, IActionable, IFrameworkTransactionEventListener, IPerspectiveListener2 {

   public static final String VIEW_ID = "org.eclipse.osee.ats.hyper.ActionSkyWalker";

   public ActionSkyWalker() {
      PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener(this);
      PlatformUI.getWorkbench().getActiveWorkbenchWindow().addPerspectiveListener(this);
   }

   @Override
   public void createPartControl(Composite parent) {
      super.createPartControl(parent);

      sashForm.setWeights(new int[] {99, 1});
      OseeEventManager.addListener(this);
   }

   @Override
   protected void createActions() {
      IActionBars bars = getViewSite().getActionBars();
      // IMenuManager mm = bars.getMenuManager();
      IToolBarManager tbm = bars.getToolBarManager();

      Action action = new Action() {
         @Override
         public void run() {
            redraw();
         }
      };
      action.setText("Refresh");
      action.setToolTipText("Refresh");
      action.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.REFRESH));
      tbm.add(action);
   }

   @Override
   public void dispose() {
      OseeEventManager.removeListener(this);
      super.dispose();
   }

   @Override
   public void explore(Artifact artifact) {
      if (artifact == null || artifact.isDeleted() || (!(artifact instanceof ATSArtifact))) clear();
      try {
         getOptions().setArtifact(artifact);
         getOptions().setLayout(getOptions().getLayout(SkyWalkerOptions.RADIAL_DOWN_LAYOUT));
         if (artifact instanceof User)
            super.explore(artifact);
         else if (artifact instanceof ATSArtifact)
            super.explore(getTopArtifact((ATSArtifact) artifact));
         else
            throw new OseeCoreException("Unexpected artifact subclass");
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         clear();
      }
   }

   public ATSArtifact getTopArtifact(ATSArtifact art) throws OseeCoreException {
      ATSArtifact artifact = null;
      if (art instanceof ActionArtifact)
         artifact = art;
      else if (art instanceof TeamWorkFlowArtifact) {
         artifact = ((TeamWorkFlowArtifact) art).getParentActionArtifact();
      } else if (art instanceof TaskArtifact) {
         Artifact parentArtifact = ((TaskArtifact) art).getParentSMA();
         if (parentArtifact instanceof TeamWorkFlowArtifact)
            artifact = ((TeamWorkFlowArtifact) parentArtifact).getParentActionArtifact();
         else {
            OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Unknown parent " + art.getHumanReadableId());
         }
      }
      return artifact;
   }

   public boolean activeEditorIsActionEditor() {
      IWorkbenchPage page = getSite().getWorkbenchWindow().getActivePage();
      if (page == null) return false;
      IEditorPart editorPart = page.getActiveEditor();
      boolean result = (editorPart != null && (editorPart instanceof SMAEditor));
      return result;
   }

   public void processWindowActivated() {
      if (!this.getSite().getPage().isPartVisible(this)) return;
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      if (page != null) {
         IEditorPart editor = page.getActiveEditor();
         if (editor != null && (editor instanceof SMAEditor)) {
            explore(((SMAEditor) editor).getSma());
         }
         clear();
      }
   }

   public void processWindowDeActivated(IWorkbenchPart part) {
      processWindowActivated();
   }

   public void partActivated(IWorkbenchPart part) {
      processWindowActivated();
   }

   public void partBroughtToTop(IWorkbenchPart part) {
      processWindowActivated();
   }

   public void partClosed(IWorkbenchPart part) {
      if (part.equals(this))
         dispose();
      else
         processWindowActivated();
   }

   public void partDeactivated(IWorkbenchPart part) {
      processWindowActivated();
   }

   public void partOpened(IWorkbenchPart part) {
      processWindowActivated();
   }

   @Override
   public String getActionDescription() {
      if (getOptions() != null && getOptions().getArtifact() != null && getOptions().getArtifact().isDeleted()) return String.format(
            "Current Artifact - %s - %s", getOptions().getArtifact().getGuid(), getOptions().getArtifact().getName());
      return "";
   }

   public void clear() {
      System.out.println("clear viewer here");
   }

   @Override
   public void handleFrameworkTransactionEvent(Sender sender, FrameworkTransactionData transData) throws OseeCoreException {
      if (sender.isRemote()) return;
      if (transData.branchId != AtsUtil.getAtsBranch().getId()) return;
      if (getOptions().getArtifact() == null) return;
      if (transData.isDeleted(getOptions().getArtifact())) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               clear();
            }
         });
      }
      if (transData.isChanged(getOptions().getArtifact()) || transData.isRelAddedChangedDeleted(getOptions().getArtifact())) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               explore(getOptions().getArtifact());
            }
         });
      }
   }

   public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
      processWindowActivated();
   }

   public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId) {
      processWindowActivated();
   }

   public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, IWorkbenchPartReference partRef, String changeId) {
      processWindowActivated();
   }

}
