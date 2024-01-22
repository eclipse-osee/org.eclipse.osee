/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.ats.ide.navigate;

import java.io.File;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.swt.program.Program;

/**
 * @author Donald G. Dunne
 */
public class ShowWorldColumnTokens extends XNavigateItemAction {

   public ShowWorldColumnTokens() {
      super("Show Workflow Column Tokens", AtsImage.REPORT, XNavigateItem.UTILITY);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      String bq = AtsApiService.get().getServerEndpoints().getWorldEndpoint().getColumnsJson();
      Lib.writeStringToFile(bq, new File("out.json"));
      Program.launch("out.json");
   }
}
