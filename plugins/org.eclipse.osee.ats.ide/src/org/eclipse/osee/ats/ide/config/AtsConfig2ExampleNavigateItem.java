/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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

   public AtsConfig2ExampleNavigateItem(XNavigateItem parent) {
      super(parent, "AtsConfig2 Example Configuration", FrameworkImage.GEAR);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {

      AtsConfig2DataExample config = new AtsConfig2DataExample();
      AtsConfig2Operation op = new AtsConfig2Operation(config);
      Operations.executeWork(op);

   }

}
