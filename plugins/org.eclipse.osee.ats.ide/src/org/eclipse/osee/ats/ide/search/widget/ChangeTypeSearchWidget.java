/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class ChangeTypeSearchWidget extends AbstractXComboViewerSearchWidget<String> {

   public static final String CHANGE_TYPE = "Change Type";

   public ChangeTypeSearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(CHANGE_TYPE, searchItem);
   }

   @Override
   public void set(AtsSearchData data) {
      if (getWidget() != null) {
         setup(getWidget());
         String changeType = data.getChangeType();
         if (Strings.isValid(changeType)) {
            getWidget().setSelected(Arrays.asList(changeType));
         }
      }
   }

   @Override
   public Collection<String> getInput() {
      List<String> cTypes = ChangeTypes.getValuesStrs();
      cTypes.remove(ChangeTypes.None.getName());
      return cTypes;
   }
}
