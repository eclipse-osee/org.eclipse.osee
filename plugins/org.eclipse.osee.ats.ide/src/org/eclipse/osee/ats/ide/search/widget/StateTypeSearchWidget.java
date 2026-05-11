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
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;

/**
 * @author Donald G. Dunne
 */
public class StateTypeSearchWidget extends AbstractXHyperlinkWfdSearchWidget<StateType> {

   public static SearchWidget PriorityWidget = new SearchWidget(3923248, "State Type", "XHyperlinkWfdForObject");

   public StateTypeSearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(PriorityWidget, searchItem);
   }

   @Override
   public void set(AtsSearchData data) {
      if (getWidget() != null) {
         List<StateType> stateTypes = data.getStateTypes();
         getWidget().setSelected(stateTypes);
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

   public void set(StateType type) {
      if (isMultiSelect()) {
         getWidget().setSelected(Arrays.asList(type));
      } else {
         getWidget().setSelected(type);
      }
   }

   @Override
   public void widgetCreated(XWidget xWidget) {
      super.widgetCreated(xWidget);
      getWidget().setSelected(Collections.asList(StateType.Working));
   }
}
