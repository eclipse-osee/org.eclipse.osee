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

package org.eclipse.osee.ats.ide.search.internal.query;

import java.util.List;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.query.AtsSearchUtil;
import org.eclipse.osee.ats.api.query.IAtsSearchDataProvider;
import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * @author Donald G. Dunne
 */
public class AtsDefaultSearchDataProvider implements IAtsSearchDataProvider {

   private JaxRsApi jaxRsApi;

   public void setJaxRsApi(JaxRsApi jaxRsApi) {
      this.jaxRsApi = jaxRsApi;
   }

   @Override
   public AtsSearchData fromJson(String namespace, String jsonValue) {
      try {
         return jaxRsApi.readValue(convertFrom25To26(jsonValue), AtsSearchData.class);
      } catch (Exception ex) {
         throw new OseeArgumentException(ex, "Unable to read AtsSearchData for [%s]", jsonValue);
      }
   }

   @Override
   public List<String> getSupportedNamespaces() {
      return AtsSearchUtil.ATS_DEFAULT_SEARCH_NAMESPACES;
   }

   @Override
   public AtsSearchData createSearchData(String namespace, String searchName) {
      AtsSearchData data = new AtsSearchData(searchName);
      data.setNamespace(namespace);
      return data;
   }
}