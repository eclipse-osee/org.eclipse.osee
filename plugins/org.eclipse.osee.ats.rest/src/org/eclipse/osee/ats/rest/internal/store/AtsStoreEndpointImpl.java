/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.ats.rest.internal.store;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.store.AtsStoreEndpointApi;

/**
 * @author Donald G. Dunne
 */
public class AtsStoreEndpointImpl implements AtsStoreEndpointApi {

   private final AtsApi atsApi;

   public AtsStoreEndpointImpl(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public void setSequence(String id, String num) {
      atsApi.getStoreService().setSequence(id, num);
   }
}
