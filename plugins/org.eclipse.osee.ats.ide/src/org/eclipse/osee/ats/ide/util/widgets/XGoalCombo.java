/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.ide.util.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.NamedComparator;
import org.eclipse.osee.framework.jdk.core.util.SortOrder;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
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
         List<ArtifactToken> sortedGoals = new ArrayList<>();
         Collection<ArtifactToken> goalArtifacts =
            ArtifactQuery.getArtifactTokenListFromTypeAndAttribute(AtsArtifactTypes.Goal,
               AtsAttributeTypes.CurrentStateType, StateType.Working.name(), AtsApiService.get().getAtsBranch());
         sortedGoals.addAll(goalArtifacts);
         Collection<ArtifactToken> backlogArtifacts =
            ArtifactQuery.getArtifactTokenListFromTypeAndAttribute(AtsArtifactTypes.AgileBacklog,
               AtsAttributeTypes.CurrentStateType, StateType.Working.name(), AtsApiService.get().getAtsBranch());
         sortedGoals.addAll(backlogArtifacts);

         Collections.sort(sortedGoals, new NamedComparator(SortOrder.ASCENDING));
         getComboViewer().setInput(sortedGoals);
         ArrayList<Object> defaultSelection = new ArrayList<>();
         defaultSelection.add("--select--");
         setSelected(defaultSelection);
         addXModifiedListener(new XModifiedListener() {

            @Override
            public void widgetModified(XWidget widget) {
               selectedGoal = AtsApiService.get().getQueryServiceIde().getArtifact(getSelected());
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
