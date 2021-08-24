/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.ide.config;

import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;

/**
 * Create ATS Configuration. See {@link AtsConfig2DataExample} for details.
 *
 * @author Donald G. Dunne
 */
public class AtsConfig2ExampleNavigateItem extends XNavigateItemAction {

   public AtsConfig2ExampleNavigateItem() {
      super("AtsConfig2 Example Configuration", FrameworkImage.GEAR, XNavigateItem.DEMO);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {

      AtsConfig2DataExample config = new AtsConfig2DataExample();
      AtsConfig2Operation op = new AtsConfig2Operation(config);
      Operations.executeWork(op);

   }

}
