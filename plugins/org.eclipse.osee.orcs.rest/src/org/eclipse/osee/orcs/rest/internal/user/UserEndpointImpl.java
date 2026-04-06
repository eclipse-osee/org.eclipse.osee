/*********************************************************************
 * Copyright (c) 2025 Boeing
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
package org.eclipse.osee.orcs.rest.internal.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.mail.Message;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.EmailRecipientInfo;
import org.eclipse.osee.framework.core.data.UserGroupArtifactToken;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.data.UserTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.util.IOseeEmail;
import org.eclipse.osee.framework.core.util.SendEmailRequest;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.model.UserEndpoint;
import org.eclipse.osee.orcs.utility.EmailCertificateService;

/**
 * @author Donald G. Dunne
 */
public class UserEndpointImpl implements UserEndpoint {

   private final OrcsApi orcsApi;
   private final EmailCertificateService emailCertificateService;

   public UserEndpointImpl(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
      this.emailCertificateService = orcsApi.getEmailCertificateService();
   }

   @Override
   public UserTokens get() {
      UserTokens toks = new UserTokens();
      for (ArtifactReadable art : orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andTypeEquals(
         CoreArtifactTypes.User).follow(CoreRelationTypes.Users_Artifact).asArtifacts()) {
         String email = art.getSoleAttributeValue(CoreAttributeTypes.Email, "");
         String userId = art.getSoleAttributeValue(CoreAttributeTypes.UserId, "");
         boolean active = art.getSoleAttributeValue(CoreAttributeTypes.Active, false);
         List<String> loginIds = art.getAttributeValues(CoreAttributeTypes.LoginId);
         UserToken user =
            UserToken.create(art.getId(), art.getName(), email, userId, active, loginIds, new ArrayList<>());
         for (ArtifactReadable roleArt : art.getRelated(CoreRelationTypes.Users_Artifact)) {
            user.getRoles().add(UserGroupArtifactToken.valueOf(roleArt.getId(), roleArt.getName()));
         }
         toks.getUsers().add(user);
      }
      return toks;
   }

   @Override
   public void uploadPublicCertificate(String certificatePem) {
      emailCertificateService.setPublicCertificateForCurrentUser(certificatePem);
   }

   @Override
   public Response getPublicCertificate() {
      String pemString = emailCertificateService.getPublicCertificateForCurrentUser();

      if (pemString == null || pemString.isBlank()) {
         return Response.noContent().build();
      }

      return Response.ok(pemString, MediaType.TEXT_PLAIN).header("Content-Disposition",
         "attachment; filename=\"public-cert.pem\"").build();
   }

   @Override
   public void deletePublicCertificate() {
      emailCertificateService.deletePublicCertificateForCurrentUser();
   }

   @Override
   public List<EmailRecipientInfo> getPublicCertificatesByEmailAddresses(Collection<String> emailAddresses) {
      return emailCertificateService.getPublicCertificatesByEmailAddresses(emailAddresses);
   }

   @Override
   public XResultData sendEmail(SendEmailRequest request) {
      XResultData rd = new XResultData();
      try {
         IOseeEmail emailMessage = orcsApi.getEmailService().create(request.getToAddresses(), request.getFromAddress(),
            request.getReplyToAddress(), request.getSubject(), request.getBody(), request.getBodyType(),
            request.getEmailAddressesAbridged(), request.getSubjectAbridged(), request.getBodyAbridged());

         if (request.getCcAddresses() != null && !request.getCcAddresses().isEmpty()) {
            emailMessage.setRecipients(Message.RecipientType.CC, request.getCcAddresses().toArray(new String[0]));
         }
         if (request.getBccAddresses() != null && !request.getBccAddresses().isEmpty()) {
            emailMessage.setRecipients(Message.RecipientType.BCC, request.getBccAddresses().toArray(new String[0]));
         }

         emailMessage.send(rd);
      } catch (Exception ex) {
         rd.error(Lib.exceptionToString(ex));
      }
      return rd;
   }

}
