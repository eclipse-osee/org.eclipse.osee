/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.util.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.core.config.GoalSorter;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class XGoalCombo extends XComboViewer {
   public static final String WIDGET_ID = XGoalCombo.class.getSimpleName();
   private Artifact selectedGoal = null;

   public XGoalCombo() {
      super("Goal", SWT.READ_ONLY);
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      super.createControls(parent, horizontalSpan);

      try {
         Collection<Artifact> goalArtifacts = org.eclipse.osee.framework.jdk.core.util.Collections.castAll(
            AtsClientService.get().getQueryService().createQuery(WorkItemType.Goal).andStateType(
               StateType.Working).getResultArtifacts().getList());
         List<IAtsGoal> sortedGoals = new ArrayList<>();
         for (Artifact goalArt : goalArtifacts) {
            sortedGoals.add((IAtsGoal) goalArt);
         }
         Collections.sort(sortedGoals, new GoalSorter());
         getComboViewer().setInput(sortedGoals);
         ArrayList<Object> defaultSelection = new ArrayList<>();
         defaultSelection.add("--select--");
         setSelected(defaultSelection);
         addXModifiedListener(new XModifiedListener() {

            @Override
            public void widgetModified(XWidget widget) {
               selectedGoal = AtsClientService.get().getQueryServiceClient().getArtifact(getSelected());
            }
         });
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public Artifact getSelectedGoal() {
      return selectedGoal;
   }

}
