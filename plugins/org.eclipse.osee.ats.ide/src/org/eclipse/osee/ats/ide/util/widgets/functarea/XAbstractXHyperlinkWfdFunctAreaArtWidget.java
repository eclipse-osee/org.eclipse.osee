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
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.ui.skynet.widgets.xchild.XAbstractXHyperlinkWfdSelectedUserGroupWithNotifyArtWidget;

/**
 * @author Donald G. Dunne
 */
public abstract class XAbstractXHyperlinkWfdFunctAreaArtWidget extends XAbstractXHyperlinkWfdSelectedUserGroupWithNotifyArtWidget {

   public XAbstractXHyperlinkWfdFunctAreaArtWidget(WidgetId widgetId, String label, ArtifactToken parentArt) {
      super(widgetId, label, parentArt);
   }

   @Override
   protected String getEmailBody(ArtifactToken selected) {
      IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) getArtifact();
      return FunctionalAreaUtil.getEmailBody(teamWf, selected);
   }

   @Override
   protected String getEmailBodyAbridged(ArtifactToken selected) {
      IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) getArtifact();
      return FunctionalAreaUtil.getEmailBodyAbridged(teamWf, selected);
   }

   @Override
   protected String getEmailSubject(ArtifactToken selected) {
      IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) getArtifact();
      return FunctionalAreaUtil.getEmailSubject(teamWf, selected);
   }

   @Override
   protected String getEmailSubjectAbridged(ArtifactToken selected) {
      IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) getArtifact();
      return FunctionalAreaUtil.getEmailSubjectAbridged(teamWf, selected);
   }

}
