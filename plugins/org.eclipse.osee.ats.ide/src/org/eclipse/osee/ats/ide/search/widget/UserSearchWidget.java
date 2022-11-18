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
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;

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
         AtsUser user = AtsApiService.get().getUserService().getUserByUserId(userId);
         XComboViewer combo = getWidget();
         combo.getLabelWidget().setLayoutData(new GridData(SWT.NONE));
         combo.setSelected(Arrays.asList(user));
      }
   }

   @Override
   public Collection<AtsUser> getInput() {
      return Collections.castAll(AtsApiService.get().getUserService().getUsers(Active.Both));
   }

   @Override
   public AtsUser get() {
      Object obj = super.get();
      if (obj instanceof AtsUser) {
         return (AtsUser) obj;
      }
      return null;
   }

}
