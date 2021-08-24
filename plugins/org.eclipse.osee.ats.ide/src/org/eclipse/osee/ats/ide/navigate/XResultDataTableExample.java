/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.navigate;

import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

/**
 * @author Donald G. Dunne
 */
public class XResultDataTableExample extends XNavigateItemAction {

   public static final String TITLE = "XResultData REST Table Example";

   public XResultDataTableExample() {
      super(TITLE, FrameworkImage.EXAMPLE, XNavigateItem.UTILITY_EXAMPLES);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      XResultData rd = AtsApiService.get().getServerEndpoints().getConfigEndpoint().getResultTableTest();
      XResultDataUI.report(rd, TITLE);
   }

}
