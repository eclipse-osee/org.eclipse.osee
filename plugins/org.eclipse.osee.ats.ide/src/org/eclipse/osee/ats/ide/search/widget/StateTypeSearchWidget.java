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
package org.eclipse.osee.ats.ide.search.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class StateTypeSearchWidget extends AbstractXComboViewerSearchWidget<String> {

   private static final String COMPLETED_CANCELLED = "Completed/Cancelled";
   private static final String WORKING_CANCELLED = "Working/Cancelled";
   private static final String WORKING_COMPLETED = "Working/Completed";
   private static final String CANCELLED = "Cancelled";
   private static final String COMPLETED = "Completed";
   private static final String WORKING = "Working";
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
         List<StateType> states = Arrays.asList(stateTypes);
         if (!states.isEmpty()) {
            boolean isWorking = false, isCompleted = false, isCancelled = false;
            if (states.contains(StateType.Working)) {
               selected = WORKING;
               isWorking = true;
            }
            if (states.contains(StateType.Completed)) {
               selected = COMPLETED;
               isCompleted = true;
            }
            if (states.contains(StateType.Cancelled)) {
               selected = CANCELLED;
               isCancelled = true;
            }
            if (isWorking && isCompleted) {
               selected = WORKING_COMPLETED;
            }
            if (isWorking && isCancelled) {
               selected = WORKING_CANCELLED;
            }
            if (isCompleted && isCancelled) {
               selected = COMPLETED_CANCELLED;
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
      return Arrays.asList(CLEAR, WORKING, COMPLETED, CANCELLED, WORKING_COMPLETED, WORKING_CANCELLED,
         COMPLETED_CANCELLED);
   }
}
