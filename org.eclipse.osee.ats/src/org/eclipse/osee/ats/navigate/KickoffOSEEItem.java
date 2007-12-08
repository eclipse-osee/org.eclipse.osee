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

import java.sql.SQLException;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;

/**
 * @author Donald G. Dunne
 */
public class KickoffOSEEItem extends XNavigateItemAction {

   public KickoffOSEEItem(XNavigateItem parent) {
      super(parent, "Kickoff OSEE");
   }

   @Override
   public void run() throws SQLException {
      //      String command = "\"C:\\Program Files\\OSEE\\eclipse.exe\" -product org.eclipse.osee.framework.ui.product.osee"
      //            + " -showsplash org.eclipse.osee.framework.ui.product -data C:\\UserData\\workspace_autorun -vmargs -XX:MaxPermSize=256m -Xmx768M -DAtsAdmin -DEmailMe";
      //      try {
      //         Process child = Runtime.getRuntime().exec(command);
      //     } catch (IOException e) {
      //     }
   }
}
