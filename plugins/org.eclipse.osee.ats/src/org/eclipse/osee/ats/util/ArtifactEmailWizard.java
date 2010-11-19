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
package org.eclipse.osee.ats.util;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.util.Overview.PreviewStyle;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.ui.skynet.util.email.EmailGroup;
import org.eclipse.osee.framework.ui.skynet.util.email.EmailWizard;

public class ArtifactEmailWizard extends EmailWizard {

   public ArtifactEmailWizard(AbstractWorkflowArtifact sma) throws OseeCoreException {
      this(sma, null);
   }

   public ArtifactEmailWizard(AbstractWorkflowArtifact sma, List<Object> toAddress) throws OseeCoreException {
      super(getPreviewHtml(sma, PreviewStyle.HYPEROPEN, PreviewStyle.NO_SUBSCRIBE_OR_FAVORITE),
         " Regarding " + sma.getArtifactTypeName() + " - " + sma.getName(), getEmailableGroups(sma), toAddress);
   }

   private static List<EmailGroup> getEmailableGroups(AbstractWorkflowArtifact workflow) throws OseeCoreException {
      ArrayList<EmailGroup> groupNames = new ArrayList<EmailGroup>();
      ArrayList<String> emails = new ArrayList<String>();
      emails.add(workflow.getCreatedBy().getEmail());
      groupNames.add(new EmailGroup("Originator", emails));
      if (workflow.getStateMgr().getAssignees().size() > 0) {
         emails = new ArrayList<String>();
         for (User u : workflow.getStateMgr().getAssignees()) {
            emails.add(u.getEmail());
         }
         groupNames.add(new EmailGroup("Assignees", emails));
      }
      return groupNames;
   }

   public static String getPreviewHtml(AbstractWorkflowArtifact workflow, PreviewStyle... styles) throws OseeCoreException {
      Overview o = new Overview();
      o.addHeader(workflow, styles);
      o.addFooter(workflow, styles);
      return o.getPage();
   }

}
