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

import org.eclipse.osee.ats.core.column.IAtsColumnUtilities;
import org.eclipse.osee.ats.core.config.IAtsConfig;
import org.eclipse.osee.ats.core.internal.AtsConfigUtility;
import org.eclipse.osee.ats.core.internal.AtsEarnedValueService;
import org.eclipse.osee.ats.core.internal.column.ev.AtsColumnUtilities;

/**
 * @author Donald G. Dunne
 */
public class AtsCore {

   private static IAtsColumnUtilities columnUtilities;

   public static IAtsConfig getAtsConfig() {
      return AtsConfigUtility.getAtsConfigProvider().getAtsConfig();
   }

   public static IAtsColumnUtilities getColumnUtilities() {
      if (columnUtilities == null) {
         columnUtilities = new AtsColumnUtilities(AtsEarnedValueService.getEarnedValueServiceProvider());
      }
      return columnUtilities;
   }
}
