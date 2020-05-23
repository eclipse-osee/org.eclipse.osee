/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.workdef;

import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

/**
 * @author Donald G. Dunne
 */
public class ValidateWorkDefinitionNavigateItem extends XNavigateItem {

   public ValidateWorkDefinitionNavigateItem(XNavigateItem parent) {
      super(parent, "Validate Work Definitions", AtsImage.WORK_DEFINITION);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      XResultData results = AtsClientService.get().getWorkDefinitionService().validateWorkDefinitions();
      XResultDataUI.report(results, getName());
   }

}
