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

import java.sql.SQLException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSArtifact;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.editor.SMAEditor;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.LocalTransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.RemoteTransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.event.TransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.TransactionEvent.EventData;
import org.eclipse.osee.framework.ui.plugin.event.Event;
import org.eclipse.osee.framework.ui.plugin.event.IEventReceiver;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.skywalker.SkyWalkerOptions;
import org.eclipse.osee.framework.ui.skynet.skywalker.SkyWalkerView;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
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
public class ActionSkyWalker extends SkyWalkerView implements IPartListener, IActionable, IEventReceiver, IPerspectiveListener2 {

   public static final String VIEW_ID = "org.eclipse.osee.ats.hyper.ActionSkyWalker";

   public ActionSkyWalker() {
      PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener(this);
      PlatformUI.getWorkbench().getActiveWorkbenchWindow().addPerspectiveListener(this);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.skywalker.SkyWalkerView#createPartControl(org.eclipse.swt.widgets.Composite)
    */
   @Override
   public void createPartControl(Composite parent) {
      super.createPartControl(parent);

      sashForm.setWeights(new int[] {99, 1});
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.skywalker.SkyWalkerView#createActions()
    */
   @Override
   protected void createActions() {
      IActionBars bars = getViewSite().getActionBars();
      // IMenuManager mm = bars.getMenuManager();
      IToolBarManager tbm = bars.getToolBarManager();

      Action action = new Action() {
         public void run() {
            redraw();
         }
      };
      action.setText("Refresh");
      action.setToolTipText("Refresh");
      action.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("refresh.gif"));
      tbm.add(action);
   }

   @Override
   public void dispose() {
      super.dispose();
      SkynetEventManager.getInstance().unRegisterAll(this);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.skywalker.SkyWalkerView#explore(org.eclipse.osee.framework.skynet.core.artifact.Artifact)
    */
   @Override
   public void explore(Artifact artifact) {
      SkynetEventManager.getInstance().unRegisterAll(this);
      if (artifact == null || artifact.isDeleted() || (!(artifact instanceof ATSArtifact))) clear();
      try {
         getOptions().setArtifact(artifact);
         getOptions().setLayout(getOptions().getLayout(SkyWalkerOptions.SPRING_LAYOUT));
         if (artifact instanceof User)
            super.explore(artifact);
         else
            super.explore(getTopArtifact((ATSArtifact) artifact));
         SkynetEventManager.getInstance().register(RemoteTransactionEvent.class, this);
         SkynetEventManager.getInstance().register(LocalTransactionEvent.class, this);
      } catch (SQLException ex) {
         clear();
         SkynetEventManager.getInstance().unRegisterAll(this);
      }
   }

   public ATSArtifact getTopArtifact(ATSArtifact art) throws SQLException {
      ATSArtifact artifact = null;
      if (art instanceof ActionArtifact)
         artifact = art;
      else if (art instanceof TeamWorkFlowArtifact) {
         artifact = ((TeamWorkFlowArtifact) art).getParentActionArtifact();
      } else if (art instanceof TaskArtifact) {
         Artifact parentArtifact = ((TaskArtifact) art).getParentSMA();
         if (parentArtifact instanceof StateMachineArtifact) {
            if (parentArtifact instanceof TeamWorkFlowArtifact)
               artifact = ((TeamWorkFlowArtifact) parentArtifact).getParentActionArtifact();
            else
               OSEELog.logSevere(AtsPlugin.class, "Unknown parent " + art.getHumanReadableId(), true);
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
            explore((ATSArtifact) ((SMAEditor) editor).getSmaMgr().getSma());
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

   public String getActionDescription() {
      if (getOptions() != null && getOptions().getArtifact() != null && getOptions().getArtifact().isDeleted()) return String.format(
            "Current Artifact - %s - %s", getOptions().getArtifact().getGuid(),
            getOptions().getArtifact().getDescriptiveName());
      return "";
   }

   public void clear() {
      System.out.println("clear viewer here");
   }

   public boolean runOnEventInDisplayThread() {
      return true;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.jdk.core.event.IEventReceiver#onEvent(org.eclipse.osee.framework.jdk.core.event.Event)
    */
   public void onEvent(Event event) {
      if (event instanceof TransactionEvent) {
         EventData ed = ((TransactionEvent) event).getEventData(getOptions().getArtifact());
         if (ed.isRemoved()) {
            clear();
         } else if (ed.getAvie() != null && ed.getAvie().getOldVersion().equals(getOptions().getArtifact())) {
            explore((StateMachineArtifact) ed.getAvie().getNewVersion());
         } else if (ed.getAvie() != null || ed.isModified() || ed.isRelChange()) {
            explore(getOptions().getArtifact());
         }
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.IPerspectiveListener#perspectiveActivated(org.eclipse.ui.IWorkbenchPage,
    *      org.eclipse.ui.IPerspectiveDescriptor)
    */
   public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
      processWindowActivated();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.IPerspectiveListener#perspectiveChanged(org.eclipse.ui.IWorkbenchPage,
    *      org.eclipse.ui.IPerspectiveDescriptor, java.lang.String)
    */
   public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId) {
      processWindowActivated();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.IPerspectiveListener2#perspectiveChanged(org.eclipse.ui.IWorkbenchPage,
    *      org.eclipse.ui.IPerspectiveDescriptor, org.eclipse.ui.IWorkbenchPartReference,
    *      java.lang.String)
    */
   public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, IWorkbenchPartReference partRef, String changeId) {
      processWindowActivated();
   }

}
