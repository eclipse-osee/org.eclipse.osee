/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.ats.ide.util.widgets.functarea;

import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactToken;

public class FunctionalAreaUtil {

   public static String FUNCT_AREA_LABEL = "Functional Area";

   public static String getEmailBody(IAtsTeamWorkflow teamWf, ArtifactToken selFunctArea) {
      return String.format("OSEE CR Functional Area set to [%s] for CR<br/><br/>" //
         + "[%s]-[%s]<br/><br/>" //
         + "This email sent to [%s] Functional Area User Group.<br/><br/>" //
         + "Select %s to see.", selFunctArea, //
         teamWf.getAtsId(), teamWf.getName(), //
         selFunctArea, //
         AtsApiService.get().getWorkItemService().getHtmlUrl(teamWf, AtsApiService.get()));
   }

   public static String getEmailSubject(IAtsTeamWorkflow teamWf, ArtifactToken selFunctArea) {
      return String.format("OSEE CR Functional Area Set for %s", teamWf.toStringWithAtsId());
   }

}
