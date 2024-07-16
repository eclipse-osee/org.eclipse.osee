/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.ats.ide.workflow.review;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.review.ReviewFormalType;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.ide.actions.newaction.NewActionUtil;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.util.IsEnabled;
import org.eclipse.osee.framework.ui.skynet.widgets.XCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.checkbox.CheckBoxStateFilteredTreeViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.checkbox.ICheckBoxStateTreeListener;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * @author Donald G. Dunne
 */
public class NewPeerReviewDialog extends EntryDialog implements IsEnabled {

   private String relatedToState = "", reviewFormalType = "", blockingType = "";
   private final Collection<String> relatedToStates;
   private final Collection<IAtsActionableItem> selectedAis;
   private final String defaultRelatedToState;
   private final Collection<IAtsActionableItem> ais;
   private CheckBoxStateFilteredTreeViewer<IAtsActionableItem> treeViewer;
   private XCombo relatedToStateCombo;
   private XCombo blockingTypeCombo;

   public NewPeerReviewDialog(String dialogTitle, String dialogMessage, Collection<String> relatedToStates, String defaultRelatedToState, Collection<IAtsActionableItem> ais) {
      super(dialogTitle, dialogMessage);
      this.relatedToStates = relatedToStates;
      this.defaultRelatedToState = defaultRelatedToState;
      this.ais = ais;
      selectedAis = new HashSet<>();
   }

   @Override
   protected void createExtendedArea(Composite parent) {

      Composite comboComp = new Composite(parent, SWT.NONE);
      comboComp.setLayout(new GridLayout(6, false));
      GridData gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
      gd.widthHint = 750;
      gd.horizontalSpan = 2;
      comboComp.setLayoutData(gd);

      createReviewTypeCombo(comboComp);
      if (!isStandaloneReview()) {
         createRelatedToCombo(comboComp);
         createBlockingTypeCombo(comboComp);
      } else {
         createAisSelection(parent);
      }
   }

   private boolean isStandaloneReview() {
      return ais != null && !ais.isEmpty();
   }

   private void createAisSelection(Composite parent) {
      Pair<CheckBoxStateFilteredTreeViewer<IAtsActionableItem>, Text> results =
         NewActionUtil.createActionableItemTreeViewer(parent, null);
      treeViewer = results.getFirst();
      treeViewer.setEnabledChecker(this);

      treeViewer.addCheckListener(new ICheckBoxStateTreeListener() {
         @Override
         public void checkStateNodesChanged() {
            selectedAis.clear();
            for (Object obj : treeViewer.getChecked()) {
               selectedAis.add((IAtsActionableItem) obj);
            }
            handleModified();
         }
      });
   }

   public Set<IAtsActionableItem> getSelectedActionableItems() {
      Set<IAtsActionableItem> selected = new HashSet<>();
      for (Object obj : treeViewer.getChecked()) {
         selected.add((IAtsActionableItem) obj);
      }
      return selected;
   }

   private void createReviewTypeCombo(Composite parent) {
      final XCombo combo = new XCombo("Review Type");
      combo.setFillHorizontally(true);
      combo.setDataStrings(Arrays.asList(ReviewFormalType.InFormal.name(), ReviewFormalType.Formal.name()));
      combo.createWidgets(parent, 2);

      XModifiedListener listener = new XModifiedListener() {

         @Override
         public void widgetModified(XWidget widget) {
            reviewFormalType = combo.get();
            handleModified();
         }
      };
      combo.addXModifiedListener(listener);
   }

   private void createBlockingTypeCombo(Composite parent) {
      blockingTypeCombo = new XCombo("Blocking Type");
      blockingTypeCombo.setFillHorizontally(true);
      blockingTypeCombo.setDataStrings(Arrays.asList(ReviewBlockType.Commit.name(), ReviewBlockType.Transition.name()));
      blockingTypeCombo.createWidgets(parent, 2);

      XModifiedListener listener = new XModifiedListener() {

         @Override
         public void widgetModified(XWidget widget) {
            blockingType = blockingTypeCombo.get();
            handleModified();
         }
      };
      blockingTypeCombo.addXModifiedListener(listener);
   }

   private void createRelatedToCombo(Composite parent) {
      relatedToStateCombo = new XCombo("Related to State");
      relatedToStateCombo.setFillHorizontally(true);
      relatedToStateCombo.setDataStrings(relatedToStates);
      if (Strings.isValid(defaultRelatedToState)) {
         relatedToStateCombo.setDataStrings(new String[] {defaultRelatedToState});
      }
      relatedToStateCombo.createWidgets(parent, 2);

      XModifiedListener listener = new XModifiedListener() {

         @Override
         public void widgetModified(XWidget widget) {
            relatedToState = relatedToStateCombo.get();
            handleModified();
         }
      };
      relatedToStateCombo.addXModifiedListener(listener);
   }

   @Override
   public boolean isEntryValid() {
      if (!super.isEntryValid()) {
         return false;
      }
      if (!Strings.isValid(getEntry())) {
         setErrorString("Must enter Title");
         return false;
      }
      if (!Strings.isValid(getReviewFormalType())) {
         setErrorString("Must select Review Type");
         return false;
      }
      if (treeViewer != null) {
         if (selectedAis.isEmpty()) {
            setErrorString("Must select Actionable Item");
            return false;
         } else if (selectedAis.size() > 1) {
            setErrorString("Only select 1 Actionable Item");
            return false;
         }
         IAtsActionableItem ai = selectedAis.iterator().next();
         IAtsTeamDefinition teamDef = ai.getAtsApi().getActionableItemService().getTeamDefinitionInherited(ai);
         if (teamDef == null) {
            AWorkbench.popup("No related Team Definition for selected Actionable Item.  Choose another");
            treeViewer.setChecked(ai, false);
         }
      }
      return true;
   }

   public String getRelatedToState() {
      return relatedToState;
   }

   public String getReviewFormalType() {
      return reviewFormalType;
   }

   public String getBlockingType() {
      return blockingType;
   }

   public void setReviewTitle(String defaultReviewTitle) {
      setEntry(defaultReviewTitle);
   }

   public String getReviewTitle() {
      return getEntry();
   }

   public String getSelectedState() {
      return relatedToStateCombo.get();
   }

   @Override
   protected Control createButtonBar(Composite parent) {
      Control control = super.createButtonBar(parent);
      handleModified();
      return control;
   }

   @Override
   public boolean isEnabled(Object obj) {
      if (obj instanceof IAtsActionableItem) {
         return ((IAtsActionableItem) obj).isActive() && ((IAtsActionableItem) obj).isActionable();
      }
      return false;
   }

}
