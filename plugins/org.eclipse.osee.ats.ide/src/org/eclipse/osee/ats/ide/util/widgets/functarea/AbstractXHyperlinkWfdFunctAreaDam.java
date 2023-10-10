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
import org.eclipse.osee.framework.ui.skynet.widgets.xchild.AbstractXHyperlinkWfdSelectedUserGroupWithNotifyDam;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractXHyperlinkWfdFunctAreaDam extends AbstractXHyperlinkWfdSelectedUserGroupWithNotifyDam {

   public AbstractXHyperlinkWfdFunctAreaDam(String label, ArtifactToken parentArt) {
      super(label, parentArt);
   }

   @Override
   protected String getEmailBody(ArtifactToken selected) {
      IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) artifact;
      return FunctionalAreaUtil.getEmailBody(teamWf, selected);
   }

   @Override
   protected String getEmailSubject(ArtifactToken selected) {
      IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) artifact;
      return FunctionalAreaUtil.getEmailSubject(teamWf, selected);
   }

}
