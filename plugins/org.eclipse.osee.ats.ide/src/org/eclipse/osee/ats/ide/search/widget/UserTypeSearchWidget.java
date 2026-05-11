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
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.query.AtsSearchUserType;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;

/**
 * @author Donald G. Dunne
 */
public class UserTypeSearchWidget extends AbstractXHyperlinkWfdSearchWidget<AtsSearchUserType> {

   public static SearchWidget UserTypeWidget = new SearchWidget(982384, "User Type", "XHyperlinkWfdForObject");

   public UserTypeSearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(UserTypeWidget, searchItem);
   }

   @Override
   public void set(AtsSearchData data) {
      if (data.getUserType() != null) {
         getWidget().setSelected(data.getUserType());
      }
   }

   @Override
   public Collection<AtsSearchUserType> getSelectable() {
      return Arrays.asList(AtsSearchUserType.Assignee, AtsSearchUserType.Originated, AtsSearchUserType.Favorites,
         AtsSearchUserType.Subscribed);
   }

   @Override
   boolean isMultiSelect() {
      return false;
   }

}
