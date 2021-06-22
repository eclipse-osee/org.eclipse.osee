/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.workflow.cr.sibling.operation;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.AtsApiService;

/**
 * @author Donald G. Dunne
 */
public class CreateSiblingOffTaskEstOperation {

   private final IAtsTeamWorkflow teamWf;
   private final AtsApi atsApi;

   public CreateSiblingOffTaskEstOperation(IAtsTeamWorkflow teamWf) {
      this.teamWf = teamWf;
      this.atsApi = AtsApiService.get();
   }

   public void run() {
      // TBD
   }

}
