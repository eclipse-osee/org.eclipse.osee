/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.framework.ui.skynet.links;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.account.rest.model.Link;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;

/**
 * @author Donald G. Dunne
 */
public class AddNewLinkNavigateItem extends XNavigateItem {

   public static final XNavItemCat LINKS = new XNavItemCat("Links");

   public AddNewLinkNavigateItem() {
      super("Add New Link", FrameworkImage.GREEN_PLUS, LINKS);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      EditLinkDialog dialog = new EditLinkDialog();
      if (dialog.open() == Window.OK) {
         Link link = new Link();
         LinkUtil.upateLinkFromDialog(dialog, link);
      }
   }

   @Override
   public Collection<IUserGroupArtifactToken> getUserGroups() {
      return Arrays.asList(CoreUserGroups.Everyone);
   }

}
