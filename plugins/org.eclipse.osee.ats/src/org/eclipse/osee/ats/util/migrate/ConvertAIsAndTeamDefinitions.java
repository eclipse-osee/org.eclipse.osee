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
package org.eclipse.osee.ats.util.migrate;

import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.workdef.provider.AtsWorkDefinitionProvider;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

public class ConvertAIsAndTeamDefinitions extends XNavigateItemAction {

   public ConvertAIsAndTeamDefinitions(XNavigateItem parent) {
      super(parent, "Convert AIs and Team Definition to AtsDsl", AtsImage.WORK_DEFINITION);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      XResultData resultData = new XResultData(false);
      AtsWorkDefinitionProvider.get().convertAndOpenAIandTeamAtsDsl(resultData);
      XResultDataUI.report(resultData, getName());
   }

}
