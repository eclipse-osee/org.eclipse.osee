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

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;

/**
 * @author Donald G. Dunne
 */
public class StateNameSearchWidget extends AbstractXHyperlinkSelectionSearchWidget<String> {

   public static final String STATE_NAME = "State Name(s)";

   public StateNameSearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(STATE_NAME, searchItem);
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
   public Collection<String> getSelectable() {
      return AtsApiService.get().getWorkDefinitionService().getStateNames();
   }

   @Override
   boolean isMultiSelect() {
      return true;
   }

   @Override
   protected String getLabel() {
      return STATE_NAME;
   }
}
