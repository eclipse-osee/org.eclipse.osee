/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.rest.internal;

import org.eclipse.osee.ats.api.AtsApi;

/**
 * @author Donald G. Dunne
 */
public class AtsApiService {

   private static AtsApi atsApi;

   public void setAtsApi(AtsApi atsApi) {
      AtsApiService.atsApi = atsApi;
   }

   public static AtsApi get() {
      return atsApi;
   }

}
