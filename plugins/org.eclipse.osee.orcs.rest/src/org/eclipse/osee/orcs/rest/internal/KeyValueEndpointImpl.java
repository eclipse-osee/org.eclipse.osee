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

package org.eclipse.osee.orcs.rest.internal;

import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.model.KeyValueEndpoint;

/**
 * @author Donald G. Dunne
 */
public class KeyValueEndpointImpl implements KeyValueEndpoint {

   private final OrcsApi orcsApi;

   public KeyValueEndpointImpl(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   @Override
   public String getByKey(Long key) {
      try {
         return orcsApi.keyValueSvc().getByKey(Id.valueOf(key));
      } catch (Exception ex) {
         return String.format("Exception storing value %s", Lib.exceptionToString(ex));
      }
   }

   @Override
   public XResultData putWithKeyIfAbsent(Long key, String value) {
      try {
         orcsApi.keyValueSvc().putWithKeyIfAbsent(key, value);
      } catch (Exception ex) {
         XResultData rd = new XResultData();
         rd.errorf("Exception storing value %s", Lib.exceptionToString(ex));
      }
      return XResultData.EMPTY_RD;
   }

   @Override
   public XResultData updateByKey(Long key, String value) {
      try {
         orcsApi.keyValueSvc().updateByKey(key, value);
      } catch (Exception ex) {
         XResultData rd = new XResultData();
         rd.errorf("Exception storing value %s", Lib.exceptionToString(ex));
      }
      return XResultData.EMPTY_RD;
   }

}
