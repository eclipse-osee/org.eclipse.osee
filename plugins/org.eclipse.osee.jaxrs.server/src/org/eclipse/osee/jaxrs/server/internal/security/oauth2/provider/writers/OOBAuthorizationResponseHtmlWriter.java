/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.writers;

import static org.eclipse.osee.jaxrs.server.internal.JaxRsUtils.asTemplateValue;
import static org.eclipse.osee.jaxrs.server.internal.security.oauth2.OAuthUtil.asExpirationValue;
import javax.ws.rs.ext.Provider;
import org.apache.cxf.rs.security.oauth2.common.OOBAuthorizationResponse;
import org.eclipse.osee.framework.jdk.core.type.ViewModel;
import org.eclipse.osee.jaxrs.server.internal.resources.AbstractHtmlWriter;

/**
 * @author Roberto E. Escobar
 */
@Provider
public class OOBAuthorizationResponseHtmlWriter extends AbstractHtmlWriter<OOBAuthorizationResponse> {

   //@formatter:off
   private static final String AUTH_CODE_PAGE__TEMPLATE = "authorization_code.html";
   private static final String AUTH_CODE_PAGE__CODE_TAG = "authorizationCode";
   private static final String AUTH_CODE_PAGE__CLIENT_ID_TAG = "clientId";
   private static final String AUTH_CODE_PAGE__EXPIRES_IN_TAG = "expiresIn";
   private static final String AUTH_CODE_PAGE__LOGGED_IN_AS_TAG = "loggedInAs";
   //@formatter:on

   @Override
   public Class<OOBAuthorizationResponse> getSupportedClass() {
      return OOBAuthorizationResponse.class;
   }

   @Override
   public ViewModel asViewModel(OOBAuthorizationResponse data) {
      ViewModel model = new ViewModel(AUTH_CODE_PAGE__TEMPLATE);
      model.param(AUTH_CODE_PAGE__CODE_TAG, asTemplateValue(data.getAuthorizationCode()));
      model.param(AUTH_CODE_PAGE__CLIENT_ID_TAG, asTemplateValue(data.getClientId()));
      model.param(AUTH_CODE_PAGE__EXPIRES_IN_TAG, asExpirationValue(data.getExpiresIn()));
      model.param(AUTH_CODE_PAGE__LOGGED_IN_AS_TAG, asTemplateValue(data.getUserId()));
      return model;
   }
}
