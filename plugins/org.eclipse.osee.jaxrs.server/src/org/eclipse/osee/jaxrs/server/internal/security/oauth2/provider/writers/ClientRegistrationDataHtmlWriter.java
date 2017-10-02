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
package org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.writers;

import static org.eclipse.osee.jaxrs.server.internal.JaxRsUtils.asTemplateValue;
import static org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.endpoints.ClientConstants.CLIENT_REGISTRATION__APPLICATION_GUID;
import javax.ws.rs.ext.Provider;
import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;
import org.eclipse.osee.framework.jdk.core.type.ViewModel;
import org.eclipse.osee.jaxrs.server.internal.resources.AbstractHtmlWriter;
import org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.endpoints.ClientConstants;
import org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.endpoints.ClientRegistrationData;
import org.eclipse.osee.jaxrs.server.internal.security.util.HiddenFormFields;

/**
 * @author Roberto E. Escobar
 */
@Provider
public class ClientRegistrationDataHtmlWriter extends AbstractHtmlWriter<ClientRegistrationData> {

   //@formatter:off
   private static final String CLIENT_REG_FORM__TEMPLATE = "client_registration_form.html";
   private static final String CLIENT_REG_FORM__REPLY_TO_TAG = "replyTo";
   private static final String CLIENT_REG_FORM__REGISTRATION_DECISION = "registrationDecisionKey";
   private static final String CLIENT_REG_FORM__REGISTRATION_DECISION_CANCEL = "registrationDecisionCancel";
   private static final String CLIENT_REG_FORM__REGISTRATION_DECISION_ACCEPT = "registrationDecisionAccept";
   private static final String CLIENT_REG_FORM__HIDDEN_FIELDS_SECTION_TAG = "hiddenFieldsSection";
   //@formatter:on

   @Override
   public Class<ClientRegistrationData> getSupportedClass() {
      return ClientRegistrationData.class;
   }

   @Override
   public ViewModel asViewModel(ClientRegistrationData data) {
      ViewModel model = new ViewModel(CLIENT_REG_FORM__TEMPLATE);
      model.param(CLIENT_REG_FORM__REPLY_TO_TAG, asTemplateValue(data.getReplyTo()));
      model.param(CLIENT_REG_FORM__REGISTRATION_DECISION, ClientConstants.CLIENT_REGISTRATION__DECISION_KEY);
      model.param(CLIENT_REG_FORM__REGISTRATION_DECISION_ACCEPT,
         ClientConstants.CLIENT_REGISTRATION__DECISION_REGISTER);
      model.param(CLIENT_REG_FORM__REGISTRATION_DECISION_CANCEL, ClientConstants.CLIENT_REGISTRATION__DECISION_CANCEL);
      model.param(CLIENT_REG_FORM__HIDDEN_FIELDS_SECTION_TAG,
         HiddenFormFields.newForm() //
            .add(CLIENT_REGISTRATION__APPLICATION_GUID, data.getClientGuid()) //
            .add(OAuthConstants.SESSION_AUTHENTICITY_TOKEN, data.getAuthenticityToken())//
            .build());
      return model;
   }
}
