/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.ide.search.widget;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;

/**
 * @author Donald G. Dunne
 */
public class StateTypeSearchWidget extends AbstractXHyperlinkSelectionSearchWidget<StateType> {

   public static final String STATE_TYPE = "State Type";

   public StateTypeSearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(STATE_TYPE, searchItem);
   }

   @Override
   public void set(AtsSearchData data) {
      if (getWidget() != null) {
         setup(getWidget());
         List<String> stateNames = data.getStates();
         getWidget().setSelected(stateNames);
      }
   }

   @Override
   public Collection<StateType> getSelectable() {
      return Arrays.asList(StateType.values());
   }

   @Override
   boolean isMultiSelect() {
      return true;
   }

   @Override
   protected String getLabel() {
      return STATE_TYPE;
   }

   public void set(StateType type) {
      if (isMultiSelect()) {
         getWidget().setSelected(Arrays.asList(type));
      } else {
         getWidget().setSelected(type);
      }
   }
}
