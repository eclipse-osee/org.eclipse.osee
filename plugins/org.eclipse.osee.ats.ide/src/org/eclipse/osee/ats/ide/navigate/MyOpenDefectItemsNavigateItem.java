/*********************************************************************
 * Copyright (c) 2026 Boeing
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

import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * Shows this user's resolved but not closed peer review defects.
 *
 * @author Donald G. Dunne
 */
public class MyOpenDefectItemsNavigateItem extends OpenDefectItemsNavigateItem {

   public MyOpenDefectItemsNavigateItem() {
      super("My Open Defect Items");
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      openDefectItems(AtsApiService.get().getUserService().getCurrentUser(),
         "Find all open Defects I Originated that have a Resolution.\n\nContinue?", "My Open Defect Items (Read-Only)",
         tableLoadOptions);
   }
}
