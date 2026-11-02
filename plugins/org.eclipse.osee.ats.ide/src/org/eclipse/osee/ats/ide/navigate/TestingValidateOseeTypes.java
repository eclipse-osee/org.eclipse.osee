/*********************************************************************
 * Copyright (c) 2020 Boeing
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

import java.util.Arrays;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.util.ServiceUtil;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

/**
 * @author Donald G. Dunne
 */
public class TestingValidateOseeTypes extends XNavigateItemAction {

   public TestingValidateOseeTypes() {
      super("Testing - Validate OSEE Types", AtsImage.REPORT, Arrays.asList(CoreUserGroups.OseeAdmin),
         TOP_ADMIN_TESTING);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {

      XResultData rd = new XResultData();
      try {
         rd = ServiceUtil.getOseeClient().getTypesEndpoint().getHealthReport();
      } catch (Exception ex) {
         rd.errorf(getName() + " Exception %s", Lib.exceptionToString(ex));
      }
      XResultDataUI.report(rd, getName());

   }
}
