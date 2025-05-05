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
import org.eclipse.osee.framework.jdk.core.util.AHTML;

public class FunctionalAreaUtil {

   public static String FUNCT_AREA_LABEL = "Functional Area";

   public static String getEmailSubjectAbridged(IAtsTeamWorkflow teamWf, ArtifactToken selected) {
      return String.format("OSEE Functional Area set for %s (abridged)", teamWf.getAtsId());
   }

   public static String getEmailBodyAbridged(IAtsTeamWorkflow teamWf, ArtifactToken selFunctArea) {
      return String.format("OSEE Functional Area set for %s", //
         teamWf.getAtsId());
   }

   public static String getEmailSubject(IAtsTeamWorkflow teamWf, ArtifactToken selFunctArea) {
      return String.format("OSEE Functional Area set for %s to [%s] - [%s]", teamWf.getAtsId(), selFunctArea,
         teamWf.getName());
   }

   public static String getEmailBody(IAtsTeamWorkflow teamWf, ArtifactToken selFunctArea) {
      String htmlUrl = AtsApiService.get().getWorkItemService().getHtmlUrl(teamWf, AtsApiService.get());
      String hyperlink = AHTML.getHyperlink(htmlUrl, "View");
      return String.format("%s<br/><br/>" //
         + "Title: [%s]<br/><br/>" //
         + "%s<br/><br/>" //
         , getEmailBodyAbridged(teamWf, selFunctArea), teamWf.getName(), hyperlink);
   }

}
