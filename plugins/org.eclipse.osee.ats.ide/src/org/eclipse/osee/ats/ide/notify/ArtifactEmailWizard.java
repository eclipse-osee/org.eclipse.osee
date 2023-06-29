/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.notify;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.ide.util.Overview.PreviewStyle;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.jdk.core.util.EmailGroup;
import org.eclipse.osee.framework.ui.skynet.util.email.EmailWizard;

/**
 * @author Donald G. Dunne
 */
public class ArtifactEmailWizard extends EmailWizard {

   public ArtifactEmailWizard(AbstractWorkflowArtifact sma) {
      this(sma, null);
   }

   public ArtifactEmailWizard(AbstractWorkflowArtifact sma, List<Object> toAddress) {
      super(AtsNotificationManagerUI.getPreviewHtml(sma, PreviewStyle.HYPEROPEN, PreviewStyle.NO_SUBSCRIBE_OR_FAVORITE),
         " Regarding " + sma.getArtifactTypeName() + " - " + sma.getName(), getEmailableGroups(sma), toAddress);
   }

   public static List<EmailGroup> getEmailableGroups(IAtsWorkItem workItem) {
      ArrayList<EmailGroup> groupNames = new ArrayList<>();
      ArrayList<String> emails = new ArrayList<>();
      emails.add(workItem.getCreatedBy().getEmail());
      groupNames.add(new EmailGroup("Originator", emails));
      if (workItem.getAssignees().size() > 0) {
         emails = new ArrayList<>();
         for (AtsUser user : workItem.getAssignees()) {
            if (user.isActive()) {
               emails.add(user.getEmail());
            }
         }
         groupNames.add(new EmailGroup("Assignees", emails));
      }
      return groupNames;
   }

}
