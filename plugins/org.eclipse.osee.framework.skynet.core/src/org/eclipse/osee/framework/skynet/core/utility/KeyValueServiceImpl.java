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

package org.eclipse.osee.framework.skynet.core.utility;

import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.skynet.core.OseeApiService;
import org.eclipse.osee.orcs.utility.KeyValueService;

/**
 * @author Donald G. Dunne
 */
public class KeyValueServiceImpl implements KeyValueService {

   private static KeyValueService instance;

   public static KeyValueService getInstance() {
      if (instance == null) {
         instance = new KeyValueServiceImpl();
      }
      return instance;
   }

   @Override
   public Long putIfAbsent(String value) {
      throw new UnsupportedOperationException("not available on client");
   }

   @Override
   public boolean putWithKeyIfAbsent(Long key, String value) {
      throw new UnsupportedOperationException("not available on client");
   }

   @Override
   public String getByKey(Long key) {
      return OseeApiService.serverEnpoints().getKeyValueEp().getByKey(key);
   }

   @Override
   public String getByKey(Id key) {
      return OseeApiService.serverEnpoints().getKeyValueEp().getByKey(key.getId());
   }

   @Override
   public Long getByValue(String value) {
      throw new UnsupportedOperationException("not available on client");
   }

   @Override
   public boolean putByKey(Long key, String value) {
      XResultData rd = OseeApiService.serverEnpoints().getKeyValueEp().putWithKeyIfAbsent(key, value);
      return rd.isSuccess();
   }

   @Override
   public boolean updateByKey(Long key, String value) {
      XResultData rd = OseeApiService.serverEnpoints().getKeyValueEp().updateByKey(key, value);
      return rd.isSuccess();
   }

   @Override
   public boolean putByKey(Id key, String value) {
      XResultData rd = OseeApiService.serverEnpoints().getKeyValueEp().putWithKeyIfAbsent(key.getId(), value);
      return rd.isSuccess();
   }

}
