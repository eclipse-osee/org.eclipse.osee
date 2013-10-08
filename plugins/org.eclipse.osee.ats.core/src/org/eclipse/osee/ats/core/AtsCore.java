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
package org.eclipse.osee.ats.core;

import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogFactory;
import org.eclipse.osee.ats.core.column.IAtsColumnUtilities;
import org.eclipse.osee.ats.core.config.IAtsConfig;
import org.eclipse.osee.ats.core.internal.AtsConfigUtility;
import org.eclipse.osee.ats.core.internal.AtsEarnedValueService;
import org.eclipse.osee.ats.core.internal.column.ev.AtsColumnUtilities;
import org.eclipse.osee.ats.core.internal.log.AtsLogFactory;

/**
 * @author Donald G. Dunne
 */
public class AtsCore {

   private static IAtsColumnUtilities columnUtilities;
   private static IAtsUserService userService;
   private static IAtsLogFactory logFactory;

   public static IAtsConfig getAtsConfig() {
      return AtsConfigUtility.getAtsConfigProvider().getAtsConfig();
   }

   public static IAtsColumnUtilities getColumnUtilities() {
      if (columnUtilities == null) {
         columnUtilities = new AtsColumnUtilities(AtsEarnedValueService.getEarnedValueServiceProvider());
      }
      return columnUtilities;
   }

   public static IAtsUserService getUserService() {
      return userService;
   }

   public void setUserService(IAtsUserService userService) {
      AtsCore.userService = userService;
   }

   public static IAtsLogFactory getLogFactory() {
      if (logFactory == null) {
         logFactory = new AtsLogFactory();
      }
      return logFactory;
   }

}
