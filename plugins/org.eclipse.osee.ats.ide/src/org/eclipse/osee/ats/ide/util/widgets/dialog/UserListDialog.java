/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.util.widgets.dialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.AtsUserNameComparator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Donald G. Dunne
 */
public class UserListDialog extends FilteredTreeAtsUserDialog {

   AtsApi atsApi;

   public UserListDialog(Shell parent, Active active) {
      this(parent, "Select User", active);
   }

   public UserListDialog(Shell parent, String title, Active active) {
      this(parent, title, getDefaultUsers(active, AtsApiService.get()));
   }

   public UserListDialog(Shell parent, String title, Collection<AtsUser> users) {
      super(title, title, users, new ArrayTreeContentProvider(), new LabelProvider());
      setShellStyle(getShellStyle() | SWT.RESIZE);
      atsApi = AtsApiService.get();
   }

   private static Collection<AtsUser> getDefaultUsers(Active active, AtsApi atsApi) {
      List<AtsUser> users = new ArrayList<>();
      if (active == Active.Both) {
         users.addAll(atsApi.getConfigService().getConfigurations().getIdToUser().values());
         Collections.sort(users, new AtsUserNameComparator());
      } else if (active == Active.Active) {
         for (AtsUser aUser : atsApi.getConfigService().getConfigurations().getIdToUser().values()) {
            if (aUser.isActive()) {
               users.add(aUser);
            }
         }
         Collections.sort(users, new AtsUserNameComparator());
      } else {
         for (AtsUser aUser : atsApi.getConfigService().getConfigurations().getIdToUser().values()) {
            if (!aUser.isActive()) {
               users.add(aUser);
            }
         }
         Collections.sort(users, new AtsUserNameComparator());
      }
      return users;
   }

   public static class UserArtifactLabelProvider extends ArtifactLabelProvider {

      @Override
      public String getText(Object element) {
         if (element instanceof AtsUser) {
            return ((AtsUser) element).getName();
         }
         return "Unknown Object";
      }
   }

   public AtsUser getSelection() {
      return super.getSelectedFirst();
   }
}