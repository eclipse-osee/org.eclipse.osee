/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.disposition.rest.internal;

import org.eclipse.osee.ats.api.AtsApi;

/**
 * @author Donald G. Dunne
 */
public class AtsApiService {

   private AtsApi atsApi;

   public void setAtsApi(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   public AtsApi get() {
      return atsApi;
   }

}
