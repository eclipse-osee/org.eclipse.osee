/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.account.rest.client.AccountClient;
import org.eclipse.osee.account.rest.client.AccountClient.UnsubscribeInfo;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.EmailUtil;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;

public class EmailGroupsData {

   private String replyToAddress, fromAddress;
   private String subject;
   private String body;
   private boolean bodyIsHtml;
   private final Set<Artifact> groups = new HashSet<>(5);

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

   public HashCollection<Artifact, Artifact> getUserToGroupMap()  {
      HashCollection<Artifact, Artifact> userToGroupMap = new HashCollection<>();
      for (Artifact group : groups) {
         for (Artifact user : group.getRelatedArtifacts(CoreRelationTypes.Users_User)) {
            Boolean isActive = user.getSoleAttributeValue(CoreAttributeTypes.Active);
            if (isActive) {
               userToGroupMap.put(user, group);
            }
         }
      }
      return userToGroupMap;
   }

   public Result isValid()  {
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

   public String getHtmlResult(User user)  {
      StringBuilder html = new StringBuilder();
      String customizedBody = getCustomizedBody(body, user);

      if (bodyIsHtml) {
         html.append(customizedBody);
      } else {
         html.append("<pre>");
         html.append(customizedBody);
         html.append("</pre>");
      }

      Set<String> groupsAllowed = new HashSet<>();
      for (Artifact group : groups) {
         groupsAllowed.add(group.getName());
      }

      AccountClient client = ServiceUtil.getAccountClient();
      ResultSet<UnsubscribeInfo> results = client.getUnsubscribeUris(user.getUuid(), groupsAllowed);
      for (UnsubscribeInfo entry : results) {
         String subscriptionName = entry.getName();
         URI unsubscribeUri = entry.getUnsubscribeUri();
         writeUnsubscribeSection(html, subscriptionName, unsubscribeUri.toASCIIString());
      }
      return html.toString();
   }

   private void writeUnsubscribeSection(StringBuilder html, String subscriptionName, String unsubscribeUri) {
      html.append("</br>Click <a href=\"");
      html.append(unsubscribeUri);
      html.append("\">unsubscribe</a> to stop receiving all emails for the topic <b>\"");
      html.append(subscriptionName);
      html.append("\"</b>");
   }

   private String getCustomizedBody(String bodyTemplate, User user) {
      String fullName = user.getName();
      String firstName = fullName.replaceAll("[^,]+, ([^ ]+).*", "$1");
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

}