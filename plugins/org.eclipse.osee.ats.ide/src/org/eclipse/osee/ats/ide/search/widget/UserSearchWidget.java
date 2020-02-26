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
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboViewer;

/**
 * @author Donald G. Dunne
 */
public class UserSearchWidget extends AbstractXComboViewerSearchWidget<AtsUser> {

   public static final String USER = "User";

   public UserSearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(USER, searchItem);
   }

   @Override
   public void set(AtsSearchData data) {
      setup(getWidget());
      String userId = data.getUserId();
      if (Strings.isValid(userId)) {
         AtsUser user = AtsClientService.get().getUserService().getUserById(userId);
         XComboViewer combo = getWidget();
         combo.setSelected(Arrays.asList(user));
      }
   }

   @Override
   public Collection<AtsUser> getInput() {
      return Collections.castAll(AtsClientService.get().getUserService().getUsers(Active.Both));
   }
}
