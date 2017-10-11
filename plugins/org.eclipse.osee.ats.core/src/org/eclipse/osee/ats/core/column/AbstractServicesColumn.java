/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.column;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.column.IAtsColumn;
import org.eclipse.osee.ats.core.internal.column.ev.AtsColumnService;

/**
 * Base column that provides generic exception handling and services to resolve text.
 *
 * @author Donald G. Dunne
 */
public abstract class AbstractServicesColumn implements IAtsColumn {

   protected final AtsApi atsApi;

   public AbstractServicesColumn(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public String getColumnText(IAtsObject atsObject) {
      try {
         return getText(atsObject);
      } catch (Exception ex) {
         return AtsColumnService.CELL_ERROR_PREFIX + " - " + ex.getLocalizedMessage();
      }
   }

   abstract String getText(IAtsObject atsObject) throws Exception;
}
