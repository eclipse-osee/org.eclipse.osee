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
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.account.rest.client.AccountClient.UnsubscribeInfo;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.util.OseeEmail;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.EmailUtil;

public class EmailGroupsData {

   private String replyToAddress, fromAddress;
   private String subject;
   private String body;
   private String bodyAbridged = OseeEmail.EMAIL_BODY_REDACTED_FOR_ABRIDGED_EMAIL;
   private boolean bodyIsHtml;
   private final Set<Artifact> groups = new HashSet<>(5);
   private HashCollection<Artifact, Artifact> cachedUserToGroupMap;

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

   public boolean isBodyIsHtml() {
      return bodyIsHtml;
   }

   public void setBodyIsHtml(boolean bodyIsHtml) {
      this.bodyIsHtml = bodyIsHtml;
   }

   public Set<Artifact> getGroups() {
      return groups;
   }

   public HashCollection<Artifact, Artifact> getUserToGroupMap() {
      if (cachedUserToGroupMap == null) {
         cachedUserToGroupMap = new HashCollection<>();
         for (Artifact group : groups) {
            for (Artifact user : group.getRelatedArtifacts(CoreRelationTypes.Users_User)) {
               Boolean isActive = user.getSoleAttributeValue(CoreAttributeTypes.Active);
               if (isActive) {
                  cachedUserToGroupMap.put(user, group);
               }
            }
         }
      }
      return cachedUserToGroupMap;
   }

   /**
    * Invalidate cached user-to-group map (e.g. after reloading group artifacts)
    */
   public void clearUserToGroupMapCache() {
      cachedUserToGroupMap = null;
   }

   public Result isValid() {
      String replyToAddress = getReplyToAddress();
      if (fromAddress == null || !EmailUtil.isEmailValid(fromAddress)) {
         return new Result("Must enter valid from address");
      }
      if (replyToAddress == null || !EmailUtil.isEmailValid(replyToAddress)) {
         return new Result("Must enter valid reply to address");
      }
      if (!Strings.isValid(getSubject())) {
         return new Result("Must enter subject");
      }
      if (!Strings.isValid(getBody())) {
         return new Result("Must enter body");
      }
      if (groups.isEmpty()) {
         return new Result("No groups selected");
      }
      Set<Artifact> groupArts = new HashSet<>();
      groupArts.addAll(getUserToGroupMap().getValues());
      if (groupArts.isEmpty()) {
         return new Result("No valid users in groups selected");
      }
      return Result.TrueResult;
   }

   public String getHtmlResult(String userName, List<UnsubscribeInfo> unsubscribeInfos) {
      StringBuilder html = new StringBuilder();
      String customizedBody = getCustomizedBody(body, userName);

      if (bodyIsHtml) {
         html.append(customizedBody);
      } else {
         html.append("<pre>");
         html.append(customizedBody);
         html.append("</pre>");
      }

      if (unsubscribeInfos != null && !unsubscribeInfos.isEmpty()) {
         html.append("<br/><br/>");
         for (UnsubscribeInfo entry : unsubscribeInfos) {
            writeUnsubscribeSection(html, entry.getName(), entry.getUnsubscribeUri().toASCIIString());
         }
      }
      return html.toString();
   }

   private void writeUnsubscribeSection(StringBuilder html, String subscriptionName, String unsubscribeUri) {
      html.append("<p>");
      html.append("Click <a href=\"");
      html.append(unsubscribeUri);
      html.append("\">unsubscribe</a> to stop receiving all emails for the topic <b>\"");
      html.append(subscriptionName);
      html.append("\"</b>");
      html.append("</p>");
   }

   private String getCustomizedBody(String bodyTemplate, String userName) {
      String firstName = userName.replaceAll("[^,]+, ([^ ]+).*", "$1");
      return bodyTemplate.replace("<firstName/>", firstName);
   }

   public String getReplyToAddress() {
      return replyToAddress;
   }

   public void setReplyToAddress(String replyToAddress) {
      this.replyToAddress = replyToAddress;
   }

   public String getFromAddress() {
      return fromAddress;
   }

   public void setFromAddress(String fromAddress) {
      this.fromAddress = fromAddress;
   }

   public String getBodyAbridged() {
      return bodyAbridged;
   }

   public void setBodyAbridged(String bodyAbridged) {
      this.bodyAbridged = bodyAbridged;
   }
}
