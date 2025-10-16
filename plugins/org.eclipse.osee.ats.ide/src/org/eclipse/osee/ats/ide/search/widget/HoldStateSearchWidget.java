/*********************************************************************
 * Copyright (c) 2025 Boeing
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
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.workdef.HoldState;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;

/**
 * @author Donald G. Dunne
 */
public class HoldStateSearchWidget extends AbstractXHyperlinkSelectionSearchWidget<HoldState> {

   public static final String HOLD_STATE = "Hold State";

   public HoldStateSearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(HOLD_STATE, searchItem);
   }

   @Override
   public void set(AtsSearchData data) {
      if (getWidget() != null) {
         setup(getWidget());
         HoldState holdState = data.getHoldState();
         getWidget().setSelected(holdState);
      }
   }

   @Override
   public Collection<HoldState> getSelectable() {
      return HoldState.values;
   }

   @Override
   boolean isMultiSelect() {
      return false;
   }

   @Override
   protected String getLabel() {
      return HOLD_STATE;
   }
}
