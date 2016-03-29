/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.search.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class StateTypeSearchWidget extends AbstractXComboViewerSearchWidget<String> {

   public static final String STATE_TYPE = "State Type";

   public StateTypeSearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(STATE_TYPE, searchItem);
   }

   public Collection<StateType> getTypes() {
      String value = get();
      List<StateType> states = new ArrayList<>();
      if (Strings.isValid(value)) {
         if (value.contains(StateType.Working.name())) {
            states.add(StateType.Working);
         }
         if (value.contains(StateType.Completed.name())) {
            states.add(StateType.Completed);
         }
         if (value.contains(StateType.Cancelled.name())) {
            states.add(StateType.Cancelled);
         }
      }
      return states;
   }

   public void set(StateType... stateTypes) {
      String selected = "";
      if (stateTypes != null && stateTypes.length != 0) {
         List<StateType> states = Collections.getAggregate(stateTypes);
         if (states.size() == 1) {
            boolean isWorking = false, isCompleted = false, isCancelled = false;
            if (states.contains(StateType.Working)) {
               selected = "Working";
               isWorking = true;
            } else if (states.contains(StateType.Completed)) {
               selected = "Completed";
               isCompleted = true;
            } else if (states.contains(StateType.Cancelled)) {
               selected = "Cancelled";
               isCancelled = true;
            } else if (isWorking && isCompleted) {
               selected = "Working/Completed";
            } else if (isWorking && isCancelled) {
               selected = "Working/Cancelled";
            } else if (isCompleted && isCancelled) {
               selected = "Completed/Cancelled";
            }
         }
      }
      if (Strings.isValid(selected)) {
         getWidget().setSelected(Arrays.asList(selected));
      }
   }

   @Override
   public void set(AtsSearchData data) {
      setup(getWidget());
      if (data.getStateTypes() != null && !data.getStateTypes().isEmpty()) {
         set(data.getStateTypes().toArray(new StateType[data.getStateTypes().size()]));
      } else {
         set();
      }
   }

   @Override
   public Collection<String> getInput() {
      return Arrays.asList(CLEAR, "Working", "Completed", "Cancelled", "Working/Completed", "Working/Cancelled",
         "Completed/Cancelled");
   }
}
