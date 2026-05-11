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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;

/**
 * @author Donald G. Dunne
 */
public class ChangeTypeSearchWidget extends AbstractXHyperlinkWfdSearchWidget<String> {

   public static SearchWidget ChangeTypeWidget = new SearchWidget(7, "Change Type(s)", "XHyperlinkWfdForObject");

   public ChangeTypeSearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(ChangeTypeWidget, searchItem);
   }

   @Override
   public void set(AtsSearchData data) {
      if (getWidget() != null) {
         List<String> changeTypes = data.getChangeTypes();
         getWidget().setSelected(changeTypes);
      }
   }

   @Override
   public Collection<String> getSelectable() {
      Set<String> cTypes = new HashSet<>();
      cTypes.addAll(ChangeTypes.getValuesStrs());
      cTypes.remove(ChangeTypes.None.getName());
      return cTypes;
   }

   @Override
   boolean isMultiSelect() {
      return true;
   }

}
