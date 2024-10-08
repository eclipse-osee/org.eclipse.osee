/*********************************************************************
 * Copyright (c) 2010 Boeing
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

import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class EmailActionsData {

   public static enum EmailRecipient {
      Originator,
      Assignees
   }

   private String subject;
   private String body;
   private final Set<Artifact> workflows = new HashSet<>(5);
   private EmailRecipient emailRecipient = null;
   private boolean includeCancelHyperlink = false;

   public String getSubject() {
      return subject;
   }

   public void setSubject(String subject) {
      this.subject = subject;
   }

   public String getBody() {
      return body;
   }

   public void setBody(String body) {
      this.body = body;
   }

   public Result isValid() {
      if (!Strings.isValid(getSubject())) {
         return new Result("Must enter subject");
      }
      if (!Strings.isValid(getBody())) {
         return new Result("Must enter body");
      }
      if (getEmailRecipient() == null) {
         return new Result("Must select Email Recipient");
      }
      if (workflows.isEmpty()) {
         return new Result("No workflows dropped");
      }
      for (Artifact workflow : workflows) {
         if (!(workflow instanceof AbstractWorkflowArtifact)) {
            return new Result("Only valid for Workflow Artifacts, not [%s]", workflow.getArtifactTypeName());
         }
      }
      return Result.TrueResult;
   }

   public Set<Artifact> getWorkflows() {
      return workflows;
   }

   public EmailRecipient getEmailRecipient() {
      return emailRecipient;
   }

   public void setEmailRecipient(EmailRecipient emailRecipient) {
      this.emailRecipient = emailRecipient;
   }

   public boolean isIncludeCancelHyperlink() {
      return includeCancelHyperlink;
   }

   public void setIncludeCancelHyperlink(boolean includeCancelHyperlink) {
      this.includeCancelHyperlink = includeCancelHyperlink;
   }

}
