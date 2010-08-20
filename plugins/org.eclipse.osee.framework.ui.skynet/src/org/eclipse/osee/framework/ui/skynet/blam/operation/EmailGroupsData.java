/*
 * Created on Aug 19, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilderClient;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Result;

public class EmailGroupsData {

   private String subject;
   private String body;
   private boolean bodyIsHtml;
   private final Set<Artifact> groups = new HashSet<Artifact>(5);

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

   public HashCollection<Artifact, Artifact> getUserToGroupMap() throws OseeCoreException {
      HashCollection<Artifact, Artifact> userToGroupMap = new HashCollection<Artifact, Artifact>();
      for (Artifact group : groups) {
         for (Artifact user : group.getRelatedArtifacts(CoreRelationTypes.Users_User)) {
            if (user.getSoleAttributeValue(CoreAttributeTypes.Active)) {
               userToGroupMap.put(user, group);
            }
         }
      }
      return userToGroupMap;
   }

   public Result isValid() throws OseeCoreException {
      if (!Strings.isValid(getSubject())) {
         return new Result("Must enter subject");
      }
      if (!Strings.isValid(getBody())) {
         return new Result("Must enter body");
      }
      if (groups.size() == 0) {
         return new Result("No groups selected");
      }
      Set<Artifact> groupArts = new HashSet<Artifact>();
      groupArts.addAll(getUserToGroupMap().getValues());
      if (groupArts.size() == 0) {
         return new Result("No valid users in groups selected");
      }
      return Result.TrueResult;
   }

   public String getHtmlResult(User user) throws OseeCoreException {
      StringBuilder html = new StringBuilder();

      if (bodyIsHtml) {
         html.append(body);
      } else {
         html.append("<pre>");
         html.append(body);
         html.append("</pre>");
      }

      for (Artifact group : groups) {
         html.append(String.format(
            "</br>Click <a href=\"%sosee/unsubscribe/group/%d/user/%d\">unsubscribe</a> to stop receiving all emails for the topic \"%s\"",
            HttpUrlBuilderClient.getInstance().getApplicationServerPrefix(), group.getArtId(), user.getArtId(),
            group.getName()));
      }
      return html.toString();
   }
}
