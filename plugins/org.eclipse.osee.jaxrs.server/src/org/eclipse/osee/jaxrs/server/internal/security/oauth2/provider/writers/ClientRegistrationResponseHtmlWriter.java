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
import javax.ws.rs.ext.Provider;
import org.eclipse.osee.framework.jdk.core.type.ViewModel;
import org.eclipse.osee.jaxrs.server.internal.resources.AbstractHtmlWriter;
import org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.endpoints.ClientRegistrationResponse;

/**
 * @author Roberto E. Escobar
 */
@Provider
public class ClientRegistrationResponseHtmlWriter extends AbstractHtmlWriter<ClientRegistrationResponse> {

   //@formatter:off
   private static final String CLIENT_REG_CONFIRM_PAGE__TEMPLATE = "client_registration.html";
   private static final String CLIENT_REG_CONFIRM_PAGE__RESOURCE_SERVER_NAME_TAG = "resourceServerName";
   private static final String CLIENT_REG_CONFIRM_PAGE__CLIENT_ID_TAG = "clientId";
   private static final String CLIENT_REG_CONFIRM_PAGE__CLIENT_SECRET_TAG = "clientSecret";
   //@formatter:on

   @Override
   public Class<ClientRegistrationResponse> getSupportedClass() {
      return ClientRegistrationResponse.class;
   }

   @Override
   public ViewModel asViewModel(ClientRegistrationResponse data) {
      ViewModel model = new ViewModel(CLIENT_REG_CONFIRM_PAGE__TEMPLATE);
      model.param(CLIENT_REG_CONFIRM_PAGE__RESOURCE_SERVER_NAME_TAG, asTemplateValue(data.getResourceServerName()));
      model.param(CLIENT_REG_CONFIRM_PAGE__CLIENT_ID_TAG, asTemplateValue(data.getClientId()));
      model.param(CLIENT_REG_CONFIRM_PAGE__CLIENT_SECRET_TAG, asTemplateValue(data.getClientSecret()));
      return model;
   }

}
