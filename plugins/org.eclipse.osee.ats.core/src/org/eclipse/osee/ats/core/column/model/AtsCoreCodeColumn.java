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

package org.eclipse.osee.ats.core.column.model;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.column.AtsCoreCodeColumnToken;
import org.eclipse.osee.ats.api.column.AtsCoreColumn;
import org.eclipse.osee.ats.core.internal.column.AtsColumnService;

/**
 * Base column that provides generic exception handling and services to resolve text.
 *
 * @author Donald G. Dunne
 */
public abstract class AtsCoreCodeColumn extends AtsCoreColumn {

   public AtsCoreCodeColumn(AtsCoreCodeColumnToken columnToken, AtsApi atsApi) {
      super(columnToken, atsApi);
   }

   @Override
   public String getColumnText(IAtsObject atsObject) {
      try {
         return getText(atsObject);
      } catch (Exception ex) {
         return AtsColumnService.CELL_ERROR_PREFIX + " - " + ex.getLocalizedMessage();
      }
   }

   protected abstract String getText(IAtsObject atsObject) throws Exception;

   @Override
   public AtsCoreCodeColumnToken getColumnToken() {
      return (AtsCoreCodeColumnToken) columnToken;
   }

}
