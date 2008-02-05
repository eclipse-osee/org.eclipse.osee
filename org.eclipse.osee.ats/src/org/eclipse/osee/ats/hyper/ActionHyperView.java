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
import java.util.logging.Level;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.ActionDebug;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSArtifact;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.editor.SMAEditor;
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.LocalTransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.RemoteTransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.event.TransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.TransactionEvent.EventData;
import org.eclipse.osee.framework.ui.plugin.event.Event;
import org.eclipse.osee.framework.ui.plugin.event.IEventReceiver;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.ats.AtsOpenOption;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener2;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class ActionHyperView extends HyperView implements IPartListener, IActionable, IEventReceiver, IPerspectiveListener2 {

   public static String VIEW_ID = "org.eclipse.osee.ats.hyper.ActionHyperView";
   private static String HELP_CONTEXT_ID = "atsActionView";
   private static ActionHyperItem topAHI;
   private static ATSArtifact currentArtifact;
   private SkynetEventManager eventManager;
   private ActionDebug debug = new ActionDebug(false, "ActionHyperView");

   public ActionHyperView() {
      super();
      debug.report("ActionHyperView");
      PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener(this);
      PlatformUI.getWorkbench().getActiveWorkbenchWindow().addPerspectiveListener(this);
      setNodeColor(whiteColor);
      setCenterColor(whiteColor);
      setVerticalSelection(45);
      eventManager = SkynetEventManager.getInstance();
   }

   @Override
   public void createPartControl(Composite top) {
      debug.report("createPartControl");
      if (!DbConnectionExceptionComposite.dbConnectionIsOk(top)) {
         PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().removePartListener(this);
         PlatformUI.getWorkbench().getActiveWorkbenchWindow().removePerspectiveListener(this);
         return;
      }
      super.createPartControl(top);
      OseeAts.addBugToViewToolbar(this, this, AtsPlugin.getInstance(), VIEW_ID, "SkyWalker");
      AtsPlugin.getInstance().setHelp(top, HELP_CONTEXT_ID);
      AtsPlugin.getInstance().setHelp(composite, HELP_CONTEXT_ID);
   }

   public static ActionHyperView getArtifactHyperView() {
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      try {
         IViewReference ivr = page.findViewReference(ActionHyperView.VIEW_ID);
         if (ivr != null && page.isPartVisible(ivr.getPart(false))) return (ActionHyperView) page.showView(ActionHyperView.VIEW_ID);
      } catch (PartInitException e1) {
         MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Launch Error",
               "Couldn't Get OSEE Hyper View " + e1.getMessage());
      }
      return null;
   }

   @Override
   public boolean provideBackForwardActions() {
      return false;
   }

   @Override
   public void dispose() {
      super.dispose();
      eventManager.unRegisterAll(this);
   }

   @Override
   public void handleItemDoubleClick(HyperViewItem hvi) {
      Artifact art = ((ActionHyperItem) hvi).getArtifact();
      if (art.isDeleted()) {
         AWorkbench.popup("ERROR", "Artifact has been deleted");
         return;
      }
      getContainer().setCursor(new Cursor(null, SWT.CURSOR_WAIT));
      AtsLib.openAtsAction(art, AtsOpenOption.OpenOneOrPopupSelect);
   }

   @Override
   public void display() {
      try {
         getContainer().setCursor(new Cursor(null, SWT.NONE));
         debug.report("display");
         eventManager.unRegisterAll(this);
         if (currentArtifact == null || currentArtifact.isDeleted()) return;
         eventManager.register(RemoteTransactionEvent.class, this);
         eventManager.register(LocalTransactionEvent.class, this);
         ATSArtifact topArt = getTopArtifact(currentArtifact);
         if (topArt == null || topArt.isDeleted()) return;
         topAHI = new ActionHyperItem(topArt);
         if (topArt instanceof ActionArtifact) {
            for (TeamWorkFlowArtifact pia : ((ActionArtifact) topArt).getTeamWorkFlowArtifacts()) {
               ActionHyperItem productAHI = new ActionHyperItem(pia);
               productAHI.setRelationToolTip("Team");
               topAHI.addBottom(productAHI);
            }
         }

         if (activeEditorIsActionEditor()) setCurrentArtifact();
         create(topAHI);
      } catch (SQLException ex) {
         clear();
      }
   }

   public void setCurrentArtifact() {
      debug.report("setCurrentArtifact");
      if (currentArtifact.equals(topAHI.getArtifact()))
         topAHI.setCurrent(true);
      else {
         topAHI.setCurrent(false);
      }
      for (ActionHyperItem child : topAHI.getChildren()) {
         if (child.getArtifact().equals(currentArtifact))
            child.setCurrent(true);
         else
            child.setCurrent(false);
         for (ActionHyperItem childChild : child.getChildren()) {
            if (childChild.getArtifact().equals(currentArtifact))
               childChild.setCurrent(true);
            else
               childChild.setCurrent(false);
         }
      }
   }

   public ATSArtifact getTopArtifact(ATSArtifact art) throws SQLException {
      debug.report("getTopArtifact");
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
            currentArtifact = (ATSArtifact) ((SMAEditor) editor).getSmaMgr().getSma();
            display();
         } else
            super.clear();
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
      if (currentArtifact != null && currentArtifact.isDeleted()) return String.format("Current Artifact - %s - %s",
            currentArtifact.getGuid(), currentArtifact.getDescriptiveName());
      return "";
   }

   @Override
   protected void clear() {
      super.clear();
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
      debug.report("onEvent");
      if (event instanceof TransactionEvent) {
         EventData ed = ((TransactionEvent) event).getEventData(currentArtifact);
         if (ed.isRemoved()) {
            clear();
         } else if (ed.getAvie() != null && ed.getAvie().getOldVersion().equals(currentArtifact)) {
            currentArtifact = (StateMachineArtifact) ed.getAvie().getNewVersion();
            display();
         } else if (ed.getAvie() != null || ed.isModified() || ed.isRelChange()) {
            display();
         }
      } else
         logger.log(Level.SEVERE, "Unexpected event => " + event);
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
