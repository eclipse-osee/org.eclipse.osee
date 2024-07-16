/*********************************************************************
 * Copyright (c) 2017 Boeing
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
import java.util.List;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workdef.StateDefinitionLabelProvider;
import org.eclipse.osee.ats.ide.workdef.StateDefinitionViewSorter;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.ArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * Widget to provide a single combo box that, upon selection of state, will prompt for transition to that state. This
 * would be for cases were the Transition buttons is un-available or the transition action should be closer to the other
 * editable fields.
 *
 * @author Donald G. Dunne
 */
public class XTransitionToStateComboWidget extends XComboViewer implements ArtifactWidget {

   private Artifact artifact;
   private AbstractWorkflowArtifact awa;

   public XTransitionToStateComboWidget() {
      this("Transition To State Combo");
      setDisplayLabel(false);
   }

   public XTransitionToStateComboWidget(String displayLabel) {
      super(displayLabel, SWT.NONE);
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {

      if (artifact.isOfType(AtsArtifactTypes.AbstractWorkflowArtifact)) {
         awa = (AbstractWorkflowArtifact) artifact;
         List<Object> states = new ArrayList<>();
         for (StateDefinition nextState : AtsApiService.get().getWorkItemService().getAllToStates(awa)) {
            if (!states.contains(nextState)) {
               states.add(nextState);
            }
         }
         setInput(states);
         setLabelProvider(new StateDefinitionLabelProvider());
         setContentProvider(new ArrayContentProvider());
         setComparator(new StateDefinitionViewSorter());

         super.createControls(parent, horizontalSpan);

         StateDefinition defaultToState = AtsApiService.get().getWorkItemService().getDefaultToState(awa);

         // Set default page from workflow default
         ArrayList<Object> defaultPage = new ArrayList<>();
         if (defaultToState != null) {
            defaultPage.add(defaultToState);
            setSelected(defaultPage);
         }
         if (awa.getStateDefinition().isCancelled() && Strings.isValid(awa.getCancelledFromState())) {
            defaultPage.add(awa.getStateDefinitionByName(awa.getCancelledFromState()));
            setSelected(defaultPage);
         }
         if (awa.getStateDefinition().isCompleted() && Strings.isValid(awa.getCompletedFromState())) {
            defaultPage.add(awa.getStateDefinitionByName(awa.getCompletedFromState()));
            setSelected(defaultPage);
         }

         getCombo().setVisibleItemCount(20);

      }
   }

   @Override
   public void setArtifact(Artifact artifact) {
      this.artifact = artifact;
   }

   @Override
   public Artifact getArtifact() {
      return artifact;
   }

   @Override
   public void saveToArtifact() {
      // do nothing
   }

   @Override
   public void revert() {
      // do nothing
   }

   @Override
   public Result isDirty() {
      return Result.FalseResult;
   }

}
