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

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsOpenOption;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.client.review.AbstractReviewArtifact;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.config.ActionableItems;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.widgets.dialog.AICheckTreeDialog;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
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
public class WfeRelationsHyperlinkComposite extends Composite {

   private static RelationTypeSide[] sides = new RelationTypeSide[] {
      AtsRelationTypes.TeamWorkflowToReview_Review,
      AtsRelationTypes.TeamWorkflowToReview_Team,
      CoreRelationTypes.Supercedes_Superceded,
      CoreRelationTypes.Supercedes_Supercedes,
      CoreRelationTypes.SupportingInfo_SupportedBy,
      CoreRelationTypes.SupportingInfo_SupportingInfo,
      AtsRelationTypes.Derive_From,
      AtsRelationTypes.Derive_To,
      CoreRelationTypes.SupportingInfo_SupportingInfo,
      CoreRelationTypes.Dependency__Artifact,
      CoreRelationTypes.Dependency__Dependency};
   private AbstractWorkflowArtifact awa;
   private Label actionableItemsLabel;
   private final WorkflowEditor editor;

   public WfeRelationsHyperlinkComposite(Composite parent, int style, WorkflowEditor editor) {
      super(parent, style);
      this.editor = editor;
   }

   public void create(AbstractWorkflowArtifact sma)  {
      this.awa = sma;
      setLayout(ALayout.getZeroMarginLayout(2, false));
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      gd.widthHint = 500;
      setLayoutData(gd);
      editor.getToolkit().adapt(this);

      // Create all hyperlinks from this artifact to others of interest
      if (sma.isTeamWorkflow()) {
         for (IAtsTeamWorkflow teamWf : sma.getParentAction().getTeamWorkflows()) {
            if (!teamWf.equals(sma)) {
               createLink("This", (Artifact) teamWf.getStoreObject(), " has sibling ", sma);
            }
         }
      }
      createArtifactRelationHyperlinks("This", sma, "is reviewed by", AtsRelationTypes.TeamWorkflowToReview_Review);
      createArtifactRelationHyperlinks("This", sma, "reviews", AtsRelationTypes.TeamWorkflowToReview_Team);
      createArtifactRelationHyperlinks("This", sma, "is superceded by", CoreRelationTypes.Supercedes_Superceded);
      createArtifactRelationHyperlinks("This", sma, "supercedes", CoreRelationTypes.Supercedes_Supercedes);
      createArtifactRelationHyperlinks("This", sma, "depends on", CoreRelationTypes.Dependency__Dependency);
      createArtifactRelationHyperlinks("This", sma, "is dependency of", CoreRelationTypes.Dependency__Artifact);

      createArtifactRelationHyperlinks("This", sma, "is derived from", AtsRelationTypes.Derive_From);
      createArtifactRelationHyperlinks("This", sma, "derived", AtsRelationTypes.Derive_To);

      createArtifactRelationHyperlinks("This", sma, "is supported info for",
         CoreRelationTypes.SupportingInfo_SupportedBy);
      createArtifactRelationHyperlinks("This", sma, "has supporting info",
         CoreRelationTypes.SupportingInfo_SupportingInfo);

      // Create label for review's related actionable items (if any)
      if (sma instanceof AbstractReviewArtifact) {
         processReviewArtifact((AbstractReviewArtifact) sma);
      }

   }

   public static boolean relationExists(AbstractWorkflowArtifact smaArt)  {
      for (RelationTypeSide side : sides) {
         if (smaArt.getRelatedArtifacts(side).size() > 0) {
            return true;
         }
      }
      if (smaArt instanceof AbstractReviewArtifact && AtsClientService.get().getWorkItemService().getActionableItemService().hasActionableItems(
         smaArt)) {
         return true;
      }
      return false;
   }

   private String getCompletedCancelledString(Artifact art)  {
      if (art instanceof AbstractWorkflowArtifact && ((AbstractWorkflowArtifact) art).isCompletedOrCancelled()) {
         return " " + ((AbstractWorkflowArtifact) art).getStateMgr().getCurrentStateName() + " ";
      }
      return "";
   }

   private void createArtifactRelationHyperlinks(String prefix, Artifact thisArt, String action, RelationTypeSide relationEnum)  {
      for (final Artifact art : thisArt.getRelatedArtifacts(relationEnum)) {
         createLink(prefix, art, action, thisArt);
      }
   }

   private void createLink(String prefix, final Artifact art, String action, Artifact thisArt) {
      try {
         editor.getToolkit().createLabel(this,
            prefix + " \"" + thisArt.getArtifactTypeName() + "\" " + action + getCompletedCancelledString(
               art) + " \"" + art.getArtifactTypeName() + "\" ");
         Hyperlink link = editor.getToolkit().createHyperlink(this,
            String.format("\"%s\" - %s", art.getName().length() < 60 ? art.getName() : art.getName().substring(0, 60),
               AtsClientService.get().getAtsId(art)),
            SWT.NONE);
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
               if (AtsUtil.isAtsArtifact(art)) {
                  AtsUtil.openATSAction(art, AtsOpenOption.OpenOneOrPopupSelect);
               } else {
                  try {
                     RendererManager.open(art, PresentationType.DEFAULT_OPEN);
                  } catch (OseeCoreException ex) {
                     OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
                  }
               }
            }
         });
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private void processReviewArtifact(final AbstractReviewArtifact reviewArt)  {
      if (!AtsClientService.get().getWorkItemService().getActionableItemService().hasActionableItems(reviewArt)) {
         return;
      }
      actionableItemsLabel = editor.getToolkit().createLabel(this, "");
      Hyperlink link = editor.getToolkit().createHyperlink(this, "(Edit)", SWT.NONE);
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
            editRelatedActionableItems(reviewArt);
         }
      });
      refreshActionableItemsLabel();
   }

   private void refreshActionableItemsLabel()  {
      if (actionableItemsLabel != null && awa instanceof AbstractReviewArtifact) {
         actionableItemsLabel.setText("This \"" + ((AbstractReviewArtifact) awa).getArtifactTypeName() +
         //
            "\" is review of Actionable Items  \"" +
            //
            AtsClientService.get().getWorkItemService().getActionableItemService().getActionableItemsStr(awa) + "\" ");
      }
   }

   public void refresh()  {
      refreshActionableItemsLabel();
   }

   private void editRelatedActionableItems(final AbstractReviewArtifact reviewArt) {
      final AICheckTreeDialog diag =
         new AICheckTreeDialog("Edit Actionable Items", "Select Actionable Items for this review", Active.Active);
      try {
         Collection<IAtsActionableItem> actionableItems = ActionableItems.getUserEditableActionableItems(
            AtsClientService.get().getWorkItemService().getActionableItemService().getActionableItems(reviewArt));

         diag.setInitialSelections(actionableItems);
         if (diag.open() != 0) {
            return;
         }
         IAtsChangeSet changes = AtsClientService.get().getStoreService().createAtsChangeSet("Edit Actionable Items",
            AtsClientService.get().getUserService().getCurrentUser());
         AtsClientService.get().getWorkItemService().getActionableItemService().setActionableItems(reviewArt,
            actionableItems, changes);
         changes.execute();
         editor.onDirtied();
         refreshActionableItemsLabel();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

   }

}
