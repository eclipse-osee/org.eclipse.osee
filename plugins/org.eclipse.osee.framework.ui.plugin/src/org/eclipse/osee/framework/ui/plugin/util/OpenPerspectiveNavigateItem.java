/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.plugin.util;

import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

public class OpenPerspectiveNavigateItem extends XNavigateItem {

   private final String perspectiveId;

   public OpenPerspectiveNavigateItem(XNavigateItem parent, String perspectiveName, String perspectiveId, KeyedImage oseeImage) {
      super(parent, "Open " + perspectiveName + " Perspective", oseeImage);
      this.perspectiveId = perspectiveId;
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      AWorkbench.openPerspective(perspectiveId);
   }

}
