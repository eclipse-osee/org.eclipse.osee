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
