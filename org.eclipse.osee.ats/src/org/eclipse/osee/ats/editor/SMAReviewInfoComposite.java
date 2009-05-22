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

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.actions.NewDecisionReviewJob;
import org.eclipse.osee.ats.actions.NewPeerToPeerReviewJob;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.editor.SMAManager.TransitionOption;
import org.eclipse.osee.ats.util.Overview;
import org.eclipse.osee.ats.util.widgets.ReviewManager;
import org.eclipse.osee.ats.util.widgets.dialog.StateListAndTitleDialog;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Donald G. Dunne
 */
public class SMAReviewInfoComposite extends Composite {

   private final SMAManager smaMgr;
   private final String forStateName;
   private final ArrayList<Label> labelWidgets = new ArrayList<Label>();
   private final IManagedForm managedForm;

   public SMAReviewInfoComposite(final SMAManager smaMgr, Composite parent, IManagedForm managedForm, XFormToolkit toolkit, String forStateName) throws OseeCoreException {
      super(parent, SWT.NONE);
      this.smaMgr = smaMgr;
      this.managedForm = managedForm;
      this.forStateName = forStateName;
      setLayout(new GridLayout(4, false));
      setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      Label label = new Label(this, SWT.NONE);
      label.setText("\"" + forStateName + "\" State Reviews: ");
      SMAEditor.setLabelFonts(label, SMAEditor.getBoldLabelFont());
      label.setToolTipText("Blocking Reviews must be completed before transtion.  Select Review hyperlink to view.");
      // If ATS Admin, allow right-click to auto-complete reviews
      if (AtsPlugin.isAtsAdmin() && !AtsPlugin.isProductionDb()) {
         label.addListener(SWT.MouseUp, new Listener() {
            /* (non-Javadoc)
                         * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
                         */
            @Override
            public void handleEvent(Event event) {
               if (event.button == 3) {
                  if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Auto Complete Reviews",
                        "ATS Admin\n\nAuto Complete Reviews?")) {
                     return;
                  }
                  try {
                     SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
                     for (ReviewSMArtifact revArt : smaMgr.getReviewManager().getReviewsFromCurrentState()) {
                        if (!revArt.getSmaMgr().isCancelledOrCompleted()) {
                           if (revArt.getSmaMgr().getStateMgr().isUnAssigned()) {
                              revArt.getSmaMgr().getStateMgr().setAssignee(UserManager.getUser());
                           }
                           Result result =
                                 revArt.getSmaMgr().transitionToCompleted("", transaction,
                                       TransitionOption.OverrideTransitionValidityCheck, TransitionOption.Persist);
                           if (result.isFalse()) {
                              result.popup();
                              return;
                           }
                        }
                     }
                     transaction.execute();
                  } catch (OseeCoreException ex) {
                     OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
                  }
               }
            }
         });
      }

      Collection<ReviewSMArtifact> revArts = smaMgr.getReviewManager().getReviews(forStateName);
      if (revArts.size() == 0) {
         (new Label(this, SWT.NONE)).setText("No Reviews Created");
      }

      Hyperlink link = toolkit.createHyperlink(this, "[Add Decision Review]", SWT.NONE);
      link.addHyperlinkListener(new IHyperlinkListener() {

         public void linkEntered(HyperlinkEvent e) {
         }

         public void linkExited(HyperlinkEvent e) {
         }

         public void linkActivated(HyperlinkEvent e) {
            try {
               StateListAndTitleDialog dialog =
                     new StateListAndTitleDialog("Create Decision Review",
                           "Select state to that review will be associated with.",
                           smaMgr.getWorkFlowDefinition().getPageNames());
               dialog.setInitialSelections(new Object[] {smaMgr.getStateMgr().getCurrentStateName()});
               if (dialog.open() == 0) {
                  if (dialog.getReviewTitle() == null || dialog.getReviewTitle().equals("")) {
                     AWorkbench.popup("ERROR", "Must enter review title");
                     return;
                  }
                  NewDecisionReviewJob job =
                        new NewDecisionReviewJob((TeamWorkFlowArtifact) smaMgr.getSma(), null, dialog.getReviewTitle(),
                              dialog.getSelectedState(), null, ReviewManager.getDefaultDecisionReviewOptions(), null);
                  job.setUser(true);
                  job.setPriority(Job.LONG);
                  job.schedule();
               }
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            }
         }
      });

      link = toolkit.createHyperlink(this, "[Add Peer to Peer Review]", SWT.NONE);
      link.addHyperlinkListener(new IHyperlinkListener() {

         public void linkEntered(HyperlinkEvent e) {
         }

         public void linkExited(HyperlinkEvent e) {
         }

         public void linkActivated(HyperlinkEvent e) {
            try {
               StateListAndTitleDialog dialog =
                     new StateListAndTitleDialog("Add Peer to Peer Review",
                           "Select state to that review will be associated with.",
                           smaMgr.getWorkFlowDefinition().getPageNames());
               dialog.setInitialSelections(new Object[] {smaMgr.getStateMgr().getCurrentStateName()});
               dialog.setReviewTitle(PeerToPeerReviewArtifact.getDefaultReviewTitle(smaMgr));
               if (dialog.open() == 0) {
                  if (dialog.getReviewTitle() == null || dialog.getReviewTitle().equals("")) {
                     AWorkbench.popup("ERROR", "Must enter review title");
                     return;
                  }
                  NewPeerToPeerReviewJob job =
                        new NewPeerToPeerReviewJob((TeamWorkFlowArtifact) smaMgr.getSma(), dialog.getReviewTitle(),
                              dialog.getSelectedState());
                  job.setUser(true);
                  job.setPriority(Job.LONG);
                  job.schedule();
               }
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });
      if (revArts.size() > 0) {
         Composite workComp = toolkit.createContainer(this, 1);
         workComp.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING));
         GridData gd = new GridData();
         gd.horizontalIndent = 20;
         gd.horizontalSpan = 4;
         workComp.setLayoutData(gd);

         for (ReviewSMArtifact revArt : revArts) {
            createReviewHyperlink(workComp, managedForm, toolkit, 2, revArt, forStateName);
         }
      }
   }

   @Override
   public String toString() {
      try {
         return "SMAReviewComposite for SMA \"" + smaMgr.getSma() + "\"";
      } catch (Exception ex) {
         return "SMAReviewComposite " + ex.getLocalizedMessage();
      }
   }

   public void clearFormMessages() {
      for (Label label : labelWidgets) {
         if (Widgets.isAccessible(managedForm.getForm())) {
            managedForm.getMessageManager().removeMessage("validation.error", label);
         }
      }
   }

   public static String toHTML(final SMAManager smaMgr, String forStateName) throws OseeCoreException {
      if (smaMgr.getReviewManager().getReviews(forStateName).size() == 0) return "";
      StringBuffer html = new StringBuffer();
      try {
         html.append(AHTML.addSpace(1) + AHTML.getLabelStr(AHTML.LABEL_FONT, "\"" + forStateName + "\" State Reviews"));
         html.append(AHTML.startBorderTable(100, Overview.normalColor, ""));
         html.append(AHTML.addHeaderRowMultiColumnTable(new String[] {"Review Type", "Title", "ID"}));
         for (ReviewSMArtifact art : smaMgr.getReviewManager().getReviews(forStateName)) {
            html.append(AHTML.addRowMultiColumnTable(new String[] {art.getArtifactTypeName(), art.getDescriptiveName(),
                  art.getHumanReadableId()}));
         }
         html.append(AHTML.endBorderTable());
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return "Review Exception - " + ex.getLocalizedMessage();
      }
      return html.toString();
   }

   public String toHTML() throws OseeCoreException {
      return SMAReviewInfoComposite.toHTML(smaMgr, forStateName);
   }

   private void createReviewHyperlink(Composite comp, IManagedForm managedForm, XFormToolkit toolkit, final int horizontalSpan, final ReviewSMArtifact revArt, String forStateName) throws OseeCoreException {

      Composite workComp = toolkit.createContainer(comp, 1);
      workComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
      workComp.setLayout(ALayout.getZeroMarginLayout(3, false));

      Label strLabel = new Label(workComp, SWT.NONE);
      labelWidgets.add(strLabel);
      if (revArt.isBlocking() && !revArt.getSmaMgr().isCancelledOrCompleted()) {
         strLabel.setText("State Blocking [" + revArt.getArtifactTypeName() + "] must be completed: ");
         IMessageManager messageManager = managedForm.getMessageManager();
         if (messageManager != null) {
            messageManager.addMessage(
                  "validation.error",
                  "\"" + forStateName + "\" State has a blocking [" + revArt.getArtifactTypeName() + "] that must be completed.",
                  null, IMessageProvider.ERROR, strLabel);
         }
      } else if (!revArt.getSmaMgr().isCancelledOrCompleted()) {
         strLabel.setText("Open [" + revArt.getArtifactTypeName() + "] exists: ");
         IMessageManager messageManager = managedForm.getMessageManager();
         if (messageManager != null) {
            messageManager.addMessage("validation.error",
                  "\"" + forStateName + "\" State has an open [" + revArt.getArtifactTypeName() + "].", null,
                  IMessageProvider.WARNING, strLabel);
         }
      } else {
         strLabel.setText(revArt.getSmaMgr().getStateMgr().getCurrentStateName() + " [" + revArt.getArtifactTypeName() + "] exists: ");
      }

      String str = "[" + revArt.getDescriptiveName() + "]";
      Hyperlink hyperLabel =
            toolkit.createHyperlink(workComp, ((str.length() > 300) ? Strings.truncate(str, 300) + "..." : str),
                  SWT.NONE);
      hyperLabel.setToolTipText("Select to open review");
      hyperLabel.addListener(SWT.MouseUp, new Listener() {
         /*
          * (non-Javadoc)
          * 
          * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
          */
         public void handleEvent(Event event) {
            SMAEditor.editArtifact(revArt);
         }
      });
   }
}
