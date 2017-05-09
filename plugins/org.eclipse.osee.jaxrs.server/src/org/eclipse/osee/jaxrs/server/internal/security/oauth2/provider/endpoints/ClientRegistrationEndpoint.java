/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.endpoints;

import static org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.endpoints.ClientConstants.CLIENT_REGISTRATION__ALLOWED_GRANT_TYPES;
import static org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.endpoints.ClientConstants.CLIENT_REGISTRATION__ALLOWED_SCOPES;
import static org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.endpoints.ClientConstants.CLIENT_REGISTRATION__APPLICATION_DESCRIPTION;
import static org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.endpoints.ClientConstants.CLIENT_REGISTRATION__APPLICATION_GUID;
import static org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.endpoints.ClientConstants.CLIENT_REGISTRATION__APPLICATION_LOGO_DATA;
import static org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.endpoints.ClientConstants.CLIENT_REGISTRATION__APPLICATION_LOGO_URI;
import static org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.endpoints.ClientConstants.CLIENT_REGISTRATION__APPLICATION_NAME;
import static org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.endpoints.ClientConstants.CLIENT_REGISTRATION__APPLICATION_REDIRECT_URI;
import static org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.endpoints.ClientConstants.CLIENT_REGISTRATION__APPLICATION_URI;
import static org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.endpoints.ClientConstants.CLIENT_REGISTRATION__AUDIENCES;
import static org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.endpoints.ClientConstants.CLIENT_REGISTRATION__CERTIFICATE;
import static org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.endpoints.ClientConstants.CLIENT_REGISTRATION__DECISION_KEY;
import static org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.endpoints.ClientConstants.CLIENT_REGISTRATION__DECISION_REGISTER;
import static org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.endpoints.ClientConstants.CLIENT_REGISTRATION__IS_CONFIDENTIAL;
import java.io.InputStream;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.ContentDisposition;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.apache.cxf.jaxrs.utils.ExceptionUtils;
import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;
import org.apache.cxf.security.SecurityContext;
import org.eclipse.osee.framework.jdk.core.type.OseePrincipal;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.jaxrs.server.internal.security.oauth2.OAuthUtil;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
@Path("/client-registration")
public class ClientRegistrationEndpoint extends AbstractClientService {

   public ClientRegistrationEndpoint(Log logger) {
      super(logger);
   }

   @GET
   @Produces({"text/html", "application/xml", "application/json"})
   public Response startRegistration(@Context UriInfo uriInfo, @Context HttpHeaders headers) {
      SecurityContext sc = getAndValidateSecurityContext();
      UserSubject subject = createUserSubject(sc);

      ClientRegistrationData data = new ClientRegistrationData();
      addAuthenticityTokenToSession(data, headers.getRequestHeaders(), subject);
      personalizeData(data, subject);

      String replyTo = UriBuilder.fromPath(uriInfo.getPath()).path("complete").build().toString();
      data.setClientGuid(GUID.create());
      data.setReplyTo(replyTo);
      return Response.ok(data).build();
   }

   @POST
   @Path("/complete")
   @Consumes(MediaType.MULTIPART_FORM_DATA)
   public Response finishRegistration(@Context UriInfo uriInfo, //
      MultipartBody multiPart) {
      SecurityContext securityContext = getAndValidateSecurityContext();
      UserSubject userSubject = createUserSubject(securityContext);

      // Make sure the session is valid
      String sessionToken = multiPart.getAttachmentObject(OAuthConstants.SESSION_AUTHENTICITY_TOKEN, String.class);
      if (!compareRequestAndSessionTokens(sessionToken, userSubject)) {
         throw ExceptionUtils.toBadRequestException(null, null);
      }

      // Get the end user decision value
      String decision = multiPart.getAttachmentObject(CLIENT_REGISTRATION__DECISION_KEY, String.class);
      boolean registrationAllowed = CLIENT_REGISTRATION__DECISION_REGISTER.equals(decision);

      Response response = null;
      if (registrationAllowed) {
         response = register(uriInfo, userSubject, multiPart);
      } else {
         response = createCancellationResponse(multiPart);
      }
      return response;
   }

   private Response register(UriInfo uriInfo, UserSubject userSubject, MultipartBody multiPart) {
      String appGuid = multiPart.getAttachmentObject(CLIENT_REGISTRATION__APPLICATION_GUID, String.class);
      String appName = multiPart.getAttachmentObject(CLIENT_REGISTRATION__APPLICATION_NAME, String.class);
      String appDesc = multiPart.getAttachmentObject(CLIENT_REGISTRATION__APPLICATION_DESCRIPTION, String.class);
      String appWebsite = multiPart.getAttachmentObject(CLIENT_REGISTRATION__APPLICATION_URI, String.class);
      String appRedirects = multiPart.getAttachmentObject(CLIENT_REGISTRATION__APPLICATION_REDIRECT_URI, String.class);
      String appConfidential = multiPart.getAttachmentObject(CLIENT_REGISTRATION__IS_CONFIDENTIAL, String.class);
      String appAudiences = multiPart.getAttachmentObject(CLIENT_REGISTRATION__AUDIENCES, String.class);
      String appGrantTypes = multiPart.getAttachmentObject(CLIENT_REGISTRATION__ALLOWED_GRANT_TYPES, String.class);
      String appScopes = multiPart.getAttachmentObject(CLIENT_REGISTRATION__ALLOWED_SCOPES, String.class);
      String appCertificate = multiPart.getAttachmentObject(CLIENT_REGISTRATION__CERTIFICATE, String.class);
      String appLogoUri = multiPart.getAttachmentObject(CLIENT_REGISTRATION__APPLICATION_LOGO_URI, String.class);

      ClientFormData input = new ClientFormData(appGuid);
      input.setUserSubject(userSubject);
      input.setName(appName);
      input.setDescription(appDesc);
      input.setWebUri(appWebsite);
      input.setRedirectUris(parseMultilined(appRedirects));
      input.setConfidential(parseConfidential(appConfidential));

      input.setAllowedAudiences(parseMultilined(appAudiences));
      input.setAllowedGrantTypes(parseMultilined(appGrantTypes));
      input.setAllowedScopes(parseMultilined(appScopes));
      input.setCertificates(parseMultilined(appCertificate));
      input.setLogoUri(appLogoUri);

      Attachment att = multiPart.getAttachment(CLIENT_REGISTRATION__APPLICATION_LOGO_DATA);
      if (att != null) {
         InputStream logoContent = att.getObject(InputStream.class);
         input.setLogoContent(logoContent);

         ContentDisposition cd = att.getContentDisposition();
         if (cd != null) {
            input.setLogoParameters(cd.getParameters());
         }
      }
      OseePrincipal principal = OAuthUtil.newOseePrincipal(userSubject);
      Client client = getDataProvider().createClient(uriInfo, principal, input);

      ClientRegistrationResponse data = new ClientRegistrationResponse();
      data.setClientId(client.getClientId());
      data.setClientSecret(client.getClientSecret());
      return Response.ok(data).build();
   }

   private Response createCancellationResponse(MultipartBody body) {
      return Response.ok().build();
   }

   private boolean parseConfidential(String appConfidential) {
      // For now all clients will be confidential - which will require them to submit their secret key
      return true;
   }

   private List<String> parseMultilined(String rawData) {
      return Collections.fromString(rawData, "\\s+");
   }
}