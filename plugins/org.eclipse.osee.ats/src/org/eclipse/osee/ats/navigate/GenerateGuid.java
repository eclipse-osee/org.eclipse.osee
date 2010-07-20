/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.navigate;

import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;

public class GenerateGuid extends XNavigateItemAction {

   public GenerateGuid(XNavigateItem parent) {
      super(parent, "Generate Guid", AtsImage.REPORT);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      XResultData resultData = new XResultData();
      for (int x = 0; x < 20; x++) {
         resultData.log(GUID.create());
      }
      resultData.report("GUIDs");
   }

}
