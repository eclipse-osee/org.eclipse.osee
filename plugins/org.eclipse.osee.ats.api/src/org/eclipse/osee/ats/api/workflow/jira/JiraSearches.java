/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.ats.api.workflow.jira;

import org.eclipse.osee.ats.api.IAtsWorkItem;

/**
 * @author Donald G. Dunne
 */
public class JiraSearches {

   public static String TW_SEARCH =
      "{ \"jql\": \"team = %s AND description ~ %s \", \"startAt\": 0, \"maxResults\": 4, " //
         + "\"fields\": [ \"summary\", \"description\", \"status\", \"assignee\" ] }";

   private JiraSearches() {
      // Utility Class
   }

   public static String getTwSearch(IAtsWorkItem workItem, Integer teamId) {
      String json = String.format(TW_SEARCH, teamId.toString(), workItem.getAtsId());
      return json;
   }

}
