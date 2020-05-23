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

import java.util.List;

/**
 * @author Donald G. Dunne
 */
public interface IAtsSearchDataProvider {

   AtsSearchData fromJson(String namespace, String json);

   AtsSearchData createSearchData(String namespace, String searchName);

   List<String> getSupportedNamespaces();

   default String convertFrom25To26(String jsonValue) {
      // for backward compatibility with 0.25 release line
      // convert all attributes and remove this upon 26.0 release
      jsonValue = jsonValue.replaceAll("\"uuid\"", "\"id\"");
      jsonValue = jsonValue.replaceAll("\"teamDefUuids\"", "\"teamDefIds\"");
      jsonValue = jsonValue.replaceAll("\"aiUuids\"", "\"aiIds\"");
      jsonValue = jsonValue.replaceAll("\"versionUuid\"", "\"versionId\"");
      jsonValue = jsonValue.replaceAll("\"programUuid\"", "\"programId\"");
      jsonValue = jsonValue.replaceAll("\"insertionUuid\"", "\"insertionId\"");
      jsonValue = jsonValue.replaceAll("\"insertionActivityUuid\"", "\"insertionActivityId\"");
      jsonValue = jsonValue.replaceAll("\"workPackageUuid\"", "\"workPackageId\"");
      return jsonValue;
   }

}
