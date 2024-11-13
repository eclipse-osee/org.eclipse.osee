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
package org.eclipse.osee.ats.ide.actions.jira;

import org.eclipse.osee.ats.ide.internal.AtsApiService;

/**
 * @author Donald G. Dunne
 */
public class JiraUtil {

   public static String JIRA_BASEPATH_KEY = "JiraBasepath";

   private JiraUtil() {
      // Utility Class
   }

   public static String getJiraBasePath() {
      return AtsApiService.get().getConfigValue(JIRA_BASEPATH_KEY);
   }
}
