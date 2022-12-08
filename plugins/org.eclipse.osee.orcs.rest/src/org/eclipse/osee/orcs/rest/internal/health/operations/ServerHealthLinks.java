/*********************************************************************
 * Copyright (c) 2022 Boeing
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
package org.eclipse.osee.orcs.rest.internal.health.operations;

import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.health.HealthLinks;

/**
 * @author Donald G. Dunne
 */
public class ServerHealthLinks {

   private final OrcsApi orcsApi;

   public ServerHealthLinks(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public HealthLinks getLinks() {
      ArtifactReadable artifact = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andId(
         CoreArtifactTokens.GlobalPreferences).getArtifact();
      String json = "";
      List<String> values = artifact.getAttributeValues(CoreAttributeTypes.GeneralStringData);
      for (String value : values) {
         if (value.startsWith(OseeProperties.OSEE_HEALTH_STATUS_LINKS)) {
            json = value.replace(OseeProperties.OSEE_HEALTH_STATUS_LINKS + "=", "");
            break;
         }
      }
      if (Strings.isInValid(json)) {
         return new HealthLinks();
      }
      HealthLinks links = JsonUtil.readValue(json, HealthLinks.class);
      return links;
   }

}
