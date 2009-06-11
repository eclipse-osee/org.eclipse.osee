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
package org.eclipse.osee.ats.editor;

import java.util.logging.Level;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData;
import org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Donald G. Dunne
 */
public class SMAActionableItemHeader extends Composite implements IFrameworkTransactionEventListener {

   private static String ACTION_ACTIONABLE_ITEMS = "Actionable Items: ";
   private Hyperlink link;
   private Label label;
   private final SMAManager smaMgr;

   public SMAActionableItemHeader(Composite parent, XFormToolkit toolkit, SMAManager smaMgr) throws OseeCoreException {
      super(parent, SWT.NONE);
      this.smaMgr = smaMgr;
      try {
         final TeamWorkFlowArtifact teamWf = (TeamWorkFlowArtifact) smaMgr.getSma();

         toolkit.adapt(this);
         setLayout(ALayout.getZeroMarginLayout(2, false));
         GridData gd = new GridData(GridData.FILL_HORIZONTAL);
         gd.horizontalSpan = 4;
         setLayoutData(gd);

         link = toolkit.createHyperlink(this, ACTION_ACTIONABLE_ITEMS, SWT.NONE);
         link.setToolTipText("Edit Actionable Items for the parent Action (this may add Team Workflows)");
         link.addHyperlinkListener(new IHyperlinkListener() {

            public void linkEntered(HyperlinkEvent e) {
            }

            public void linkExited(HyperlinkEvent e) {
            }

            public void linkActivated(HyperlinkEvent e) {
               try {
                  AtsLib.editActionableItems(teamWf.getParentActionArtifact());
               } catch (Exception ex) {
                  OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         });

         label = toolkit.createLabel(this, " ");
         refresh();

         OseeEventManager.addListener(this);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   private void refresh() throws OseeCoreException {
      if (label.isDisposed()) {
         OseeEventManager.removeListener(this);
         return;
      }
      final TeamWorkFlowArtifact teamWf = (TeamWorkFlowArtifact) smaMgr.getSma();
      if (!smaMgr.isCancelled() && !smaMgr.isCompleted()) {
         if (teamWf.getParentActionArtifact().getActionableItems().size() == 0) {
            label.setText(" " + ACTION_ACTIONABLE_ITEMS + "Error: No Actionable Items identified.");
            label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
         } else {
            StringBuffer sb = new StringBuffer(teamWf.getActionableItemsDam().getActionableItemsStr());
            if (teamWf.getParentActionArtifact().getTeamWorkFlowArtifacts().size() > 1) {
               sb.append("         Other: ");
               for (TeamWorkFlowArtifact workflow : teamWf.getParentActionArtifact().getTeamWorkFlowArtifacts()) {
                  if (!workflow.equals(teamWf)) {
                     sb.append(workflow.getActionableItemsDam().getActionableItemsStr() + ", ");
                  }
               }
            }
            label.setText(sb.toString().replaceFirst(", $", ""));
            label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
         }
         label.update();
         layout();
      } else {
         if (teamWf.getParentActionArtifact().getActionableItems().size() == 0) {
            label.setText(" " + ACTION_ACTIONABLE_ITEMS + "Error: No Actionable Items identified.");
            label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
         } else {
            label.setText(" " + ACTION_ACTIONABLE_ITEMS + teamWf.getParentActionArtifact().getWorldViewActionableItems());
            label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
         }
         label.update();
         layout();
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener#handleFrameworkTransactionEvent(org.eclipse.osee.framework.skynet.core.event.Sender, org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData)
    */
   @Override
   public void handleFrameworkTransactionEvent(Sender sender, FrameworkTransactionData transData) throws OseeCoreException {
      if (smaMgr.isInTransition()) return;
      if (transData.branchId != AtsPlugin.getAtsBranch().getBranchId()) return;
      // Since SMAEditor is refreshed when a sibling workflow is changed, need to refresh this
      // list of actionable items when a sibling changes
      for (TeamWorkFlowArtifact teamWf : smaMgr.getSma().getParentActionArtifact().getTeamWorkFlowArtifacts()) {
         if (!smaMgr.getSma().equals(teamWf) && (transData.isChanged(teamWf) || transData.isRelAdded(teamWf.getParentActionArtifact()) || transData.isRelDeleted(teamWf.getParentActionArtifact()))) {
            Displays.ensureInDisplayThread(new Runnable() {
               @Override
               public void run() {
                  try {
                     refresh();
                  } catch (OseeCoreException ex) {
                     OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
                  }
               }
            });
            // Only need to refresh once
            return;
         }
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.swt.widgets.Widget#dispose()
    */
   @Override
   public void dispose() {
      super.dispose();
      OseeEventManager.removeListener(this);
   }

}
