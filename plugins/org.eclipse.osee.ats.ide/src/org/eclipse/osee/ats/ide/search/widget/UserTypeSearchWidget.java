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

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.query.AtsSearchUserType;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;

/**
 * @author Donald G. Dunne
 */
public class UserTypeSearchWidget extends AbstractXComboViewerSearchWidget<AtsSearchUserType> {

   public static final String USER_TYPE = "User Type";

   public UserTypeSearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(USER_TYPE, searchItem);
   }

   @Override
   public void set(AtsSearchData data) {
      setup(getWidget());
      if (data.getUserType() != null) {
         getWidget().setSelected(Arrays.asList(data.getUserType()));
      }
   }

   @Override
   public Collection<AtsSearchUserType> getInput() {
      return Arrays.asList(AtsSearchUserType.Assignee, AtsSearchUserType.Originated, AtsSearchUserType.Favorites,
         AtsSearchUserType.Subscribed);
   }
}
