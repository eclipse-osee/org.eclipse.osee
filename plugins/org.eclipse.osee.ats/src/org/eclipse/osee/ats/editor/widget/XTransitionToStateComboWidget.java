/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.editor.widget;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.workdef.StateDefinitionLabelProvider;
import org.eclipse.osee.ats.workdef.StateDefinitionViewSorter;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget;
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
public class XTransitionToStateComboWidget extends XComboViewer implements IArtifactWidget {

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
         for (IAtsStateDefinition nextState : awa.getToStatesWithCompleteCancelReturnStates()) {
            if (!states.contains(nextState)) {
               states.add(nextState);
            }
         }
         setInput(states);
         setLabelProvider(new StateDefinitionLabelProvider());
         setContentProvider(new ArrayContentProvider());
         setComparator(new StateDefinitionViewSorter());

         super.createControls(parent, horizontalSpan);

         IAtsStateDefinition defaultToState = awa.getStateDefinition().getDefaultToState();

         // Set default page from workflow default
         ArrayList<Object> defaultPage = new ArrayList<>();
         if (defaultToState != null) {
            defaultPage.add(defaultToState);
            setSelected(defaultPage);
         }
         if (awa.getStateDefinition().getStateType().isCancelledState() && Strings.isValid(
            awa.getCancelledFromState())) {
            defaultPage.add(awa.getStateDefinitionByName(awa.getCancelledFromState()));
            setSelected(defaultPage);
         }
         if (awa.getStateDefinition().getStateType().isCompletedState() && Strings.isValid(
            awa.getCompletedFromState())) {
            defaultPage.add(awa.getStateDefinitionByName(awa.getCompletedFromState()));
            setSelected(defaultPage);
         }

         getCombo().setVisibleItemCount(20);

      }
   }

   @Override
   public void setArtifact(Artifact artifact)  {
      this.artifact = artifact;
   }

   @Override
   public Artifact getArtifact()  {
      return artifact;
   }

   @Override
   public void saveToArtifact()  {
      // do nothing
   }

   @Override
   public void revert()  {
      // do nothing
   }

   @Override
   public Result isDirty()  {
      return Result.FalseResult;
   }

}
