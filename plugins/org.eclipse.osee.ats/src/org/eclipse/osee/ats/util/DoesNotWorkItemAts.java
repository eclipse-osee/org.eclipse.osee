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
package org.eclipse.osee.ats.util;

import org.eclipse.osee.ats.core.query.QueryTest;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;

/**
 * @author Donald G. Dunne
 */
public class DoesNotWorkItemAts extends XNavigateItemAction {

   public DoesNotWorkItemAts(XNavigateItem parent) {
      super(parent, "Does Not Work - ATS - Test AtsQuery", PluginUiImage.ADMIN);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      QueryTest test = new QueryTest(AtsClientService.get().getServices());
      test.run();
      AWorkbench.popup("done");
   }

}
