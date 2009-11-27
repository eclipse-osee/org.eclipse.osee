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

import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.widgets.dialog.AICheckTreeDialog;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.enums.CoreRelationEnumeration;
import org.eclipse.osee.framework.core.enums.IRelationEnumeration;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.ats.AtsOpenOption;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Donald G. Dunne
 */
public class SMARelationsHyperlinkComposite extends Composite {

   private final XFormToolkit toolkit;
   private static IRelationEnumeration[] sides =
         new IRelationEnumeration[] {AtsRelation.TeamWorkflowToReview_Review, AtsRelation.TeamWorkflowToReview_Team,
         CoreRelationEnumeration.Supercedes_Superceded, CoreRelationEnumeration.Supercedes_Supercedes,
         CoreRelationEnumeration.SupportingInfo_SupportedBy, CoreRelationEnumeration.SupportingInfo_SupportingInfo,
         CoreRelationEnumeration.Dependency__Artifact, CoreRelationEnumeration.Dependency__Dependency};
   private SMAManager smaMgr;
   private Label actionableItemsLabel;

   /**
    * @param parent
    * @param style
    */
   public SMARelationsHyperlinkComposite(Composite parent, XFormToolkit toolkit, int style) {
      super(parent, style);
      this.toolkit = toolkit;
   }

   public void create(SMAManager smaMgr) throws OseeCoreException {
      this.smaMgr = smaMgr;
      setLayout(ALayout.getZeroMarginLayout(2, false));
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      gd.widthHint = 500;
      setLayoutData(gd);
      toolkit.adapt(this);

      // Create all hyperlinks from this artifact to others of interest
      createArtifactRelationHyperlinks("This", smaMgr.getSma(), "is reviewed by",
            AtsRelation.TeamWorkflowToReview_Review);
      createArtifactRelationHyperlinks("This", smaMgr.getSma(), "reviews", AtsRelation.TeamWorkflowToReview_Team);
      createArtifactRelationHyperlinks("This", smaMgr.getSma(), "supercedes",
            CoreRelationEnumeration.Supercedes_Superceded);
      createArtifactRelationHyperlinks("This", smaMgr.getSma(), "is superceded by",
            CoreRelationEnumeration.Supercedes_Supercedes);
      createArtifactRelationHyperlinks("This", smaMgr.getSma(), "depends on",
            CoreRelationEnumeration.Dependency__Dependency);
      createArtifactRelationHyperlinks("This", smaMgr.getSma(), "is dependency of",
            CoreRelationEnumeration.Dependency__Artifact);
      createArtifactRelationHyperlinks("This", smaMgr.getSma(), "is supported info for",
            CoreRelationEnumeration.SupportingInfo_SupportedBy);
      createArtifactRelationHyperlinks("This", smaMgr.getSma(), "has supporting info",
            CoreRelationEnumeration.SupportingInfo_SupportingInfo);

      // Create label for review's related actionable items (if any) 
      if (smaMgr.getSma() instanceof ReviewSMArtifact) {
         processReviewArtifact((ReviewSMArtifact) smaMgr.getSma());
      }

   }

   public static boolean relationExists(StateMachineArtifact smaArt) throws OseeCoreException {
      for (IRelationEnumeration side : sides) {
         if (smaArt.getRelatedArtifacts(side).size() > 0) {
            return true;
         }
         if (smaArt.getParentActionArtifact() != null && smaArt.getParentActionArtifact().getRelatedArtifacts(side).size() > 0) {
            return true;
         }
      }
      if (smaArt instanceof ReviewSMArtifact && ((ReviewSMArtifact) smaArt).getActionableItemsDam().getActionableItemGuids().size() > 0) {
         return true;
      }
      return false;
   }

   private String getCompletedCancelledString(Artifact art) throws OseeCoreException {
      if (art instanceof StateMachineArtifact) {
         if (((StateMachineArtifact) art).getSmaMgr().isCancelledOrCompleted()) {
            return " " + ((StateMachineArtifact) art).getSmaMgr().getStateMgr().getCurrentStateName() + " ";
         }
      }
      return "";
   }

   private void createArtifactRelationHyperlinks(String prefix, Artifact thisArt, String action, IRelationEnumeration side) throws OseeCoreException {
      for (final Artifact art : thisArt.getRelatedArtifacts(side)) {
         toolkit.createLabel(
               this,
               prefix + " \"" + thisArt.getArtifactTypeName() + "\" " + action + getCompletedCancelledString(art) + " \"" + art.getArtifactTypeName() + "\" ");
         Hyperlink link =
               toolkit.createHyperlink(this, String.format("\"%s\" - %s",
               art.getName().length() < 60 ? art.getName() : art.getName().substring(0, 60),
               art.getHumanReadableId()), SWT.NONE);
         link.addHyperlinkListener(new IHyperlinkListener() {

            public void linkEntered(HyperlinkEvent e) {
            }

            public void linkExited(HyperlinkEvent e) {
            }

            public void linkActivated(HyperlinkEvent e) {
               AtsUtil.openAtsAction(art, AtsOpenOption.OpenOneOrPopupSelect);
            }
         });
      }
   }

   private void processReviewArtifact(final ReviewSMArtifact reviewArt) throws OseeCoreException {
      if (reviewArt.getActionableItemsDam().getActionableItemGuids().size() == 0) {
         return;
      }
      actionableItemsLabel = toolkit.createLabel(this, "");
      Hyperlink link = toolkit.createHyperlink(this, "(Edit)", SWT.NONE);
      link.addHyperlinkListener(new IHyperlinkListener() {

         public void linkEntered(HyperlinkEvent e) {
         }

         public void linkExited(HyperlinkEvent e) {
         }

         public void linkActivated(HyperlinkEvent e) {
            editRelatedActionableItems(reviewArt);
         }
      });
      refreshActionableItemsLabel();
   }

   private void refreshActionableItemsLabel() throws OseeCoreException {
      if (actionableItemsLabel != null && smaMgr.getSma() instanceof ReviewSMArtifact) {
         actionableItemsLabel.setText("This \"" + ((ReviewSMArtifact) smaMgr.getSma()).getArtifactTypeName() +
                        //
         "\" is review of Actionable Items  \"" +
                        //
         ((ReviewSMArtifact) smaMgr.getSma()).getActionableItemsDam().getActionableItemsStr() + "\" ");
      }
   }

   public void refresh() throws OseeCoreException {
      refreshActionableItemsLabel();
   }

   private void editRelatedActionableItems(final ReviewSMArtifact reviewArt) {
      final AICheckTreeDialog diag =
            new AICheckTreeDialog("Edit Actionable Items", "Select Actionable Items for this review", Active.Active);
      try {
         diag.setInitialSelections(reviewArt.getActionableItemsDam().getActionableItems());
         if (diag.open() != 0) {
            return;
         }
         reviewArt.getActionableItemsDam().setActionableItems(diag.getChecked());
         smaMgr.getEditor().onDirtied();
         refreshActionableItemsLabel();
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }

   }
}
