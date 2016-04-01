/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.links;

import org.eclipse.jface.window.Window;
import org.eclipse.osee.account.rest.model.Link;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;

/**
 * @author Donald G. Dunne
 */
public class AddNewLinkNavigateItem extends XNavigateItem {

   public AddNewLinkNavigateItem(XNavigateItem parent) {
      super(parent, "Add New Link", FrameworkImage.GREEN_PLUS);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      EditLinkDialog dialog = new EditLinkDialog();
      if (dialog.open() == Window.OK) {
         Link link = new Link();
         LinkUtil.upateLinkFromDialog(dialog, link);
      }
   }

}
