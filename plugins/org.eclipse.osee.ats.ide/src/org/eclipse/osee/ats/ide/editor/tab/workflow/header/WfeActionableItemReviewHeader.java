/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.editor.tab.workflow.header;

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.editor.event.IWfeEventHandle;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.widgets.dialog.AICheckTreeDialog;
import org.eclipse.osee.ats.ide.workflow.review.AbstractReviewArtifact;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
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
public class WfeActionableItemReviewHeader extends Composite implements IWfeEventHandle {

   private Label label;
   private final AbstractReviewArtifact review;

   public static boolean isApplicable(IAtsWorkItem workItem) {
      return workItem.isReview() && AtsClientService.get().getActionableItemService().hasActionableItems((workItem));
   }

   public WfeActionableItemReviewHeader(Composite parent, XFormToolkit toolkit, AbstractReviewArtifact review, final WorkflowEditor editor) {
      super(parent, SWT.NONE);
      this.review = review;
      try {
         toolkit.adapt(this);
         setLayout(ALayout.getZeroMarginLayout(2, false));
         GridData gd = new GridData(GridData.FILL_HORIZONTAL);
         gd.horizontalSpan = 4;
         setLayoutData(gd);

         label = editor.getToolkit().createLabel(this, "");
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
               editRelatedActionableItems(review);
            }
         });

         refresh();
         editor.registerEvent(this, AtsAttributeTypes.ActionableItemReference);

      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private void editRelatedActionableItems(final AbstractReviewArtifact review) {
      final AICheckTreeDialog diag =
         new AICheckTreeDialog("Edit Actionable Items", "Select Actionable Items for this review", Active.Active);
      diag.setExpandChecked(true);
      try {
         Collection<IAtsActionableItem> actionableItems =
            AtsClientService.get().getActionableItemService().getUserEditableActionableItems(
               AtsClientService.get().getActionableItemService().getActionableItems(review));
         diag.setInitialSelections(actionableItems);

         if (diag.open() != 0) {
            return;
         }
         Collection<IAtsActionableItem> checked = diag.getChecked();
         if (checked.isEmpty()) {
            AWorkbench.popup("Can't remove all Actionable Items");
            return;
         }
         IAtsChangeSet changes = AtsClientService.get().getStoreService().createAtsChangeSet("Edit Actionable Items",
            AtsClientService.get().getUserService().getCurrentUser());
         AtsClientService.get().getActionableItemService().setActionableItems(review, checked, changes);
         changes.executeIfNeeded();
         refresh();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

   }

   @Override
   public void refresh() {
      label.setText("This \"" + review.getArtifactTypeName() +
      //
         "\" is review of Actionable Items  \"" +
         //
         AtsClientService.get().getActionableItemService().getActionableItemsStr(review) + "\" ");
      label.update();
      layout();
   }

   @Override
   public IAtsWorkItem getWorkItem() {
      return review;
   }

}
