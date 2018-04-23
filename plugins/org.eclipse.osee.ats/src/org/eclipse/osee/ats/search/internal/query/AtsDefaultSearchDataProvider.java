/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.search.internal.query;

import java.util.List;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.query.AtsSearchUtil;
import org.eclipse.osee.ats.api.query.IAtsSearchDataProvider;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * @author Donald G. Dunne
 */
public class AtsDefaultSearchDataProvider implements IAtsSearchDataProvider {

   @Override
   public AtsSearchData fromJson(String namespace, String jsonValue) {
      try {
         jsonValue = convertFrom25To26(jsonValue);
         return JsonUtil.getMapper().readValue(jsonValue, AtsSearchData.class);
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
