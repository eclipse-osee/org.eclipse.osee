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

import java.sql.SQLException;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.ats.util.widgets.dialog.AICheckTreeDialog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.skynet.core.relation.IRelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.ats.AtsOpenOption;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
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
public class SMARelationsComposite extends Composite {

   private final XFormToolkit toolkit;
   private static RelationSide sides[] =
         new RelationSide[] {RelationSide.TeamWorkflowToReview_Review, RelationSide.TeamWorkflowToReview_Team,
               RelationSide.Supercedes_Superceded, RelationSide.Supercedes_Supercedes,
               RelationSide.SupportingInfo_SupportedBy, RelationSide.SupportingInfo_SupportingInfo};
   private SMAManager smaMgr;
   private Label actionableItemsLabel;

   /**
    * @param parent
    * @param style
    */
   public SMARelationsComposite(Composite parent, XFormToolkit toolkit, int style) {
      super(parent, style);
      this.toolkit = toolkit;
   }

   public void create(SMAManager smaMgr) {
      this.smaMgr = smaMgr;
      try {
         setLayout(ALayout.getZeroMarginLayout(2, false));
         GridData gd = new GridData(GridData.FILL_HORIZONTAL);
         gd.widthHint = 500;
         setLayoutData(gd);
         toolkit.adapt(this);

         processArtifact("This", smaMgr.getSma());
         if (smaMgr.getSma() instanceof ReviewSMArtifact) processReviewArtifact((ReviewSMArtifact) smaMgr.getSma());

         if ((smaMgr.getSma() instanceof TeamWorkFlowArtifact) && ((TeamWorkFlowArtifact) smaMgr.getSma()).getParentActionArtifact() != null) {
            processArtifact("Parent ", ((TeamWorkFlowArtifact) smaMgr.getSma()).getParentActionArtifact());
         }
      } catch (SQLException ex) {
         // Do nothing
      }
   }

   public static boolean relationExists(StateMachineArtifact smaArt) throws SQLException {
      for (RelationSide side : sides) {
         if (smaArt.getArtifacts(side).size() > 0) return true;
         if (smaArt.getParentActionArtifact() != null && smaArt.getParentActionArtifact().getArtifacts(side).size() > 0) return true;
      }
      if ((smaArt instanceof ReviewSMArtifact) && ((ReviewSMArtifact) smaArt).getActionableItemsDam().getActionableItemGuids().size() > 0) return true;
      return false;
   }

   private void processArtifact(String name, Artifact thisArt) throws SQLException {
      for (RelationSide side : sides) {
         for (final Artifact art : thisArt.getArtifacts(side)) {
            IRelationLink rel = thisArt.getRelations(art).iterator().next();
            toolkit.createLabel(
                  this,
                  name + " \"" + thisArt.getArtifactTypeName() + "\" " + rel.getSidePhrasingFor(thisArt) + " \"" + art.getArtifactTypeName() + "\" ");
            Hyperlink link =
                  toolkit.createHyperlink(
                        this,
                        String.format(
                              "\"%s\" - %s",
                              art.getDescriptiveName().length() < 60 ? art.getDescriptiveName() : art.getDescriptiveName().substring(
                                    0, 60), art.getHumanReadableId()), SWT.NONE);
            link.addHyperlinkListener(new IHyperlinkListener() {

               public void linkEntered(HyperlinkEvent e) {
               }

               public void linkExited(HyperlinkEvent e) {
               }

               public void linkActivated(HyperlinkEvent e) {
                  AtsLib.openAtsAction(art, AtsOpenOption.OpenOneOrPopupSelect);
               }
            });
         }
      }
   }

   private void processReviewArtifact(final ReviewSMArtifact reviewArt) throws SQLException {
      if (reviewArt.getActionableItemsDam().getActionableItemGuids().size() == 0) return;
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

   private void refreshActionableItemsLabel() {
      if ((actionableItemsLabel != null) && smaMgr.getSma() instanceof ReviewSMArtifact) actionableItemsLabel.setText("This \"" + ((ReviewSMArtifact) smaMgr.getSma()).getArtifactTypeName() + "\" is review of Actionable Items  \"" + ((ReviewSMArtifact) smaMgr.getSma()).getActionableItemsDam().getActionableItemsStr() + "\" ");
   }

   public void refresh() {
      refreshActionableItemsLabel();
   }

   private void editRelatedActionableItems(final ReviewSMArtifact reviewArt) {
      final AICheckTreeDialog diag =
            new AICheckTreeDialog("Edit Actionable Items", "Select Actionable Items for this review", Active.Active);
      try {
         diag.setInitialSelections(reviewArt.getActionableItemsDam().getActionableItems().toArray());
         if (diag.open() != 0) return;
         reviewArt.getActionableItemsDam().setActionableItems(diag.getSelection());
         smaMgr.getEditor().onDirtied();
         refreshActionableItemsLabel();
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }

   }
}
