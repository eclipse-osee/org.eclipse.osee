/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.framework.ui.plugin.util;

import org.eclipse.osee.framework.core.enums.OseeImage;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

public class OpenPerspectiveNavigateItem extends XNavigateItem {

   private final String perspectiveId;

   public OpenPerspectiveNavigateItem(XNavigateItem parent, String perspectiveName, String perspectiveId, OseeImage oseeImage) {
      this(parent, perspectiveName, perspectiveId, ImageManager.create(oseeImage));
   }

   public OpenPerspectiveNavigateItem(XNavigateItem parent, String perspectiveName, String perspectiveId, KeyedImage oseeImage) {
      super(parent, "Open " + perspectiveName + " Perspective", oseeImage);
      this.perspectiveId = perspectiveId;
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      AWorkbench.openPerspective(perspectiveId);
   }

}
