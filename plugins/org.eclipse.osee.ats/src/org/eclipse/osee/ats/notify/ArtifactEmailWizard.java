/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.notify;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.util.Overview.PreviewStyle;
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
      if (workItem.getStateMgr().getAssignees().size() > 0) {
         emails = new ArrayList<>();
         for (IAtsUser user : workItem.getStateMgr().getAssignees()) {
            emails.add(user.getEmail());
         }
         groupNames.add(new EmailGroup("Assignees", emails));
      }
      return groupNames;
   }

}
