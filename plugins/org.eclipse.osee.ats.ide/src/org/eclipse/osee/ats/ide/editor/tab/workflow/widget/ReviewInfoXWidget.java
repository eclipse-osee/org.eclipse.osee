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

package org.eclipse.osee.ats.ide.editor.tab.workflow.widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.review.ReviewFormalType;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelper;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.editor.tab.workflow.section.WfeWorkflowSection;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.Overview;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.review.AbstractReviewArtifact;
import org.eclipse.osee.ats.ide.workflow.review.NewPeerReviewDialog;
import org.eclipse.osee.ats.ide.workflow.review.NewPeerToPeerReviewJob;
import org.eclipse.osee.ats.ide.workflow.review.ReviewManager;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.widgets.XLabelValueBase;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
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
public class ReviewInfoXWidget extends XLabelValueBase {

   private final IStateToken forState;
   private final List<Label> labelWidgets = new ArrayList<>();
   private Composite destroyableComposite = null;
   private final Composite composite;
   private final IManagedForm managedForm;
   private final int horizontalSpan;
   private final XFormToolkit toolkit;
   private final TeamWorkFlowArtifact teamArt;
   private final WfeWorkflowSection workflowSection;

   public ReviewInfoXWidget(WfeWorkflowSection workflowSection, XFormToolkit toolkit, final TeamWorkFlowArtifact teamArt, final IStateToken forState, Composite composite, int horizontalSpan) {
      super("\"" + forState.getName() + "\" State Reviews");
      this.workflowSection = workflowSection;
      this.managedForm = workflowSection.getManagedForm();
      this.toolkit = toolkit;
      this.teamArt = teamArt;
      this.forState = forState;
      this.composite = composite;
      this.horizontalSpan = horizontalSpan;
      reDisplay();
   }

   public void reDisplay() {
      if (composite != null && composite.isDisposed()) {
         return;
      }
      if (destroyableComposite != null) {
         destroyableComposite.dispose();
      }
      destroyableComposite = new Composite(composite, SWT.None);
      destroyableComposite.setLayout(ALayout.getZeroMarginLayout(4, false));

      setToolTip("Blocking Reviews must be completed before transtion.  Select Review hyperlink to view.");
      createWidgets(managedForm, destroyableComposite, horizontalSpan);

      try {
         addAdminRightClickOption();
         Collection<AbstractReviewArtifact> revArts = ReviewManager.getReviews(teamArt, forState);
         if (revArts.isEmpty()) {
            setValueText("No Reviews Created");
         }

         Hyperlink link = toolkit.createHyperlink(destroyableComposite, "[Add Peer to Peer Review]", SWT.NONE);
         link.addHyperlinkListener(new IHyperlinkListener() {

            @Override
            public void linkEntered(HyperlinkEvent e) {
               // do nothing
            }

            @Override
            public void linkExited(HyperlinkEvent e) {
               // do nothing
            }

            @Override
            public void linkActivated(HyperlinkEvent e) {
               try {
                  if (workflowSection.getEditor().isDirty()) {
                     workflowSection.getEditor().doSave(null);
                  }
                  NewPeerReviewDialog dialog =
                     new NewPeerReviewDialog("Add Peer to Peer Review", "Enter Title and Select Review Type.",
                        AtsApiService.get().getWorkDefinitionService().getStateNames(teamArt.getWorkDefinition()),
                        forState.getName(), null);
                  dialog.setReviewTitle(AtsApiService.get().getReviewService().getDefaultReviewTitle(teamArt));
                  if (dialog.open() == Window.OK) {
                     if (!Strings.isValid(dialog.getReviewTitle())) {
                        AWorkbench.popup("ERROR", "Must enter review title");
                        return;
                     }
                     ReviewBlockType blockType = null;
                     if (Strings.isValid(dialog.getBlockingType())) {
                        blockType = ReviewBlockType.valueOf(dialog.getBlockingType());
                     }
                     ReviewFormalType reviewType = null;
                     if (Strings.isValid(dialog.getReviewFormalType())) {
                        reviewType = ReviewFormalType.valueOf(dialog.getReviewFormalType());
                     }
                     NewPeerToPeerReviewJob job = new NewPeerToPeerReviewJob(teamArt, null, dialog.getReviewTitle(),
                        dialog.getSelectedState(), blockType, reviewType);
                     job.setUser(true);
                     job.setPriority(Job.LONG);
                     job.schedule();
                  }
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         });
         if (revArts.size() > 0) {
            Composite workComp = toolkit.createContainer(destroyableComposite, 1);
            workComp.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING));
            GridData gd = new GridData();
            gd.horizontalIndent = 20;
            gd.horizontalSpan = 4;
            workComp.setLayoutData(gd);

            for (AbstractReviewArtifact revArt : revArts) {
               createReviewHyperlink(workComp, managedForm, toolkit, revArt, forState);
            }
         }

      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public String toString() {
      try {
         return "ReviewInfoXWidget for SMA \"" + teamArt + "\"";
      } catch (Exception ex) {
         return "ReviewInfoXWidget " + ex.getLocalizedMessage();
      }
   }

   public static String toHTML(final TeamWorkFlowArtifact teamArt, IStateToken forState) {
      if (ReviewManager.getReviews(teamArt, forState).isEmpty()) {
         return "";
      }
      StringBuffer html = new StringBuffer();
      try {
         html.append(
            AHTML.addSpace(1) + AHTML.getLabelStr(AHTML.LABEL_FONT, "\"" + forState.getName() + "\" State Reviews"));
         html.append(AHTML.startBorderTable(100, Overview.normalColor, ""));
         html.append(AHTML.addHeaderRowMultiColumnTable(new String[] {"Review Type", "Title", "ID"}));
         for (AbstractReviewArtifact art : ReviewManager.getReviews(teamArt, forState)) {
            html.append(
               AHTML.addRowMultiColumnTable(new String[] {art.getArtifactTypeName(), art.getName(), art.getAtsId()}));
         }
         html.append(AHTML.endBorderTable());
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return "Review Exception - " + ex.getLocalizedMessage();
      }
      return html.toString();
   }

   private void createReviewHyperlink(Composite comp, IManagedForm managedForm, XFormToolkit toolkit, final AbstractReviewArtifact revArt, IStateToken forState) {

      Composite workComp = toolkit.createContainer(comp, 1);
      workComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
      workComp.setLayout(ALayout.getZeroMarginLayout(3, false));

      Label strLabel = new Label(workComp, SWT.NONE);
      labelWidgets.add(strLabel);
      if (revArt.isBlocking() && !revArt.isCompletedOrCancelled()) {
         strLabel.setText("State Blocking [" + revArt.getArtifactTypeName() + "] must be completed: ");
         IMessageManager messageManager = managedForm.getMessageManager();
         if (messageManager != null) {
            messageManager.addMessage("validation.error",
               "\"" + forState.getName() + "\" State has a blocking [" + revArt.getArtifactTypeName() + "] that must be completed.",
               null, IMessageProvider.ERROR, strLabel);
         }
      } else if (!revArt.isCompletedOrCancelled()) {
         strLabel.setText("Open [" + revArt.getArtifactTypeName() + "] exists: ");
         IMessageManager messageManager = managedForm.getMessageManager();
         if (messageManager != null) {
            messageManager.addMessage("validation.error",
               "\"" + forState.getName() + "\" State has an open [" + revArt.getArtifactTypeName() + "].", null,
               IMessageProvider.WARNING, strLabel);
         }
      } else {
         strLabel.setText(
            revArt.getCurrentStateName() + " [" + revArt.getArtifactTypeName() + "] exists: ");
      }

      String str = "[" + revArt.getName() + "]";
      Hyperlink hyperLabel =
         toolkit.createHyperlink(workComp, str.length() > 300 ? Strings.truncate(str, 300) + "..." : str, SWT.NONE);
      hyperLabel.setToolTipText("Select to open review");
      hyperLabel.addListener(SWT.MouseUp, new Listener() {
         @Override
         public void handleEvent(Event event) {
            WorkflowEditor.editArtifact(revArt);
         }
      });
   }

   public void addAdminRightClickOption() {
      // If ATS Admin, allow right-click to auto-complete tasks
      if (AtsApiService.get().getUserService().isAtsAdmin() && !AtsApiService.get().getStoreService().isProductionDb()) {
         labelWidget.addListener(SWT.MouseUp, new Listener() {
            @Override
            public void handleEvent(Event event) {
               if (event.button == 3) {
                  if (!MessageDialog.openConfirm(Displays.getActiveShell(), "Auto Complete Reviews",
                     "ATS Admin\n\nAuto Complete Reviews?")) {
                     return;
                  }
                  try {
                     List<AbstractWorkflowArtifact> awas = new ArrayList<>();
                     for (IAtsAbstractReview review : ReviewManager.getReviewsFromCurrentState(teamArt)) {
                        AbstractReviewArtifact revArt =
                           (AbstractReviewArtifact) AtsApiService.get().getQueryService().getArtifact(review);
                        if (!revArt.isCompletedOrCancelled()) {
                           if (revArt.getStateMgr().isUnAssigned()) {
                              revArt.getStateMgr().setAssignee(AtsApiService.get().getUserService().getCurrentUser());
                           }
                           awas.add(revArt);
                        }
                     }
                     TransitionHelper helper = new TransitionHelper("ATS Auto Complete Reviews",
                        org.eclipse.osee.framework.jdk.core.util.Collections.castAll(awas),
                        TeamState.Completed.getName(), null, null, null, AtsApiService.get(),
                        TransitionOption.OverrideTransitionValidityCheck, TransitionOption.None);
                     TransitionResults results = AtsApiService.get().getWorkItemServiceIde().transition(helper);
                     if (!results.isEmpty()) {
                        AWorkbench.popup(String.format("Transition Error %s", results.toString()));
                     }
                     workflowSection.getEditor().refresh();
                  } catch (OseeCoreException ex) {
                     OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
                  }
               }
            }
         });
      }
   }
}
