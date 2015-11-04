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

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.workdef.AtsWorkDefinitionSheetProviders;
import org.eclipse.osee.ats.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class StateNameSearchWidget extends AbstractXComboViewerSearchWidget<String> {

   public static final String STATE_NAME = "State Name";

   public StateNameSearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(STATE_NAME, searchItem);
   }

   @Override
   public void set(AtsSearchData data) {
      setup(getWidget());
      String stateName = data.getState();
      if (Strings.isValid(stateName)) {
         getWidget().setInput(Arrays.asList(stateName));
      }
   }

   @Override
   public Collection<String> getInput() {
      return AtsWorkDefinitionSheetProviders.getAllValidStateNames();
   }
}
