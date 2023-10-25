/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.ats.rest.internal.util;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.core.util.AtsAbstractEarnedValueImpl;
import org.eclipse.osee.logger.Log;

/**
 * @author Donald G. Dunne
 */
public class AtsEarnedValueImpl extends AtsAbstractEarnedValueImpl {

   public AtsEarnedValueImpl(Log logger, AtsApi atsApi) {
      super(logger, atsApi);
   }

   @Override
   public double getEstimatedHoursFromTasks(IAtsWorkItem workItem, IStateToken relatedToState) {
      return 0;
   }

   @Override
   public double getEstimatedHoursTotal(IAtsWorkItem workItem, IStateToken relatedToState) {
      return 0;
   }

}
