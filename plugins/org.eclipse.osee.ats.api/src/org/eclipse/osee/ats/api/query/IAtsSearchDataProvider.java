/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.api.query;

import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;

/**
 * @author Donald G. Dunne
 */
public interface IAtsSearchDataProvider {

   AtsSearchData fromJson(String namespace, String json);

   AtsSearchData createSearchData(String namespace, String searchName);

   default AtsSearchData fromJson(String namespace, String jsonValue, Class<? extends AtsSearchData> searchDataClass, JaxRsApi jaxRsApi) {
      if (supportsNamespace(namespace)) {
         try {
            return jaxRsApi.readValue(jsonValue, searchDataClass);
         } catch (Exception ex) {
            throw new OseeArgumentException(ex, "Unable to read LbaAtsSearchData for [%s]", jsonValue);
         }
      }
      throw new OseeStateException("Namespace [%s] is not supported by the provider %s", namespace,
         getClass().getName());
   }

   boolean supportsNamespace(String namespace);
}