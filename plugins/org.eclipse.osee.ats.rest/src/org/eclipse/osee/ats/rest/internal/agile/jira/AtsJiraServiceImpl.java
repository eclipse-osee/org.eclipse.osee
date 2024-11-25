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
package org.eclipse.osee.ats.rest.internal.agile.jira;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.core.agile.jira.AbstractAtsJiraService;

/**
 * @author Donald G. Dunne
 */
public class AtsJiraServiceImpl extends AbstractAtsJiraService {

   public AtsJiraServiceImpl(AtsApi atsApi) {
      super(atsApi);
   }

   @Override
   protected String searchJira(String json) {
      return null;
   }

}
