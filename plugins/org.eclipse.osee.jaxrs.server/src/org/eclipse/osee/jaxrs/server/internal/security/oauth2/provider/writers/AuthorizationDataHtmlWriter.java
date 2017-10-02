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

import static org.apache.cxf.rs.security.oauth2.utils.OAuthConstants.AUTHORIZATION_DECISION_ALLOW;
import static org.apache.cxf.rs.security.oauth2.utils.OAuthConstants.AUTHORIZATION_DECISION_DENY;
import static org.apache.cxf.rs.security.oauth2.utils.OAuthConstants.AUTHORIZATION_DECISION_KEY;
import static org.eclipse.osee.jaxrs.server.internal.JaxRsUtils.asTemplateValue;
import java.util.List;
import javax.ws.rs.ext.Provider;
import org.apache.cxf.rs.security.oauth2.common.OAuthAuthorizationData;
import org.apache.cxf.rs.security.oauth2.common.OAuthPermission;
import org.apache.cxf.rs.security.oauth2.common.Permission;
import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;
import org.eclipse.osee.framework.jdk.core.type.ViewModel;
import org.eclipse.osee.jaxrs.server.internal.resources.AbstractHtmlWriter;
import org.eclipse.osee.jaxrs.server.internal.security.util.HiddenFormFields;
import org.eclipse.osee.jaxrs.server.internal.security.util.InputFields;
import org.eclipse.osee.jaxrs.server.internal.security.util.InputFields.InputType;

/**
 * @author Roberto E. Escobar
 */
@Provider
public class AuthorizationDataHtmlWriter extends AbstractHtmlWriter<OAuthAuthorizationData> {

   //@formatter:off
   private static final String AUTHORIZE_PAGE__TEMPLATE = "authorize_form.html";
   private static final String AUTHORIZE_PAGE__REPLY_TO_TAG = "replyTo";
   private static final String AUTHORIZE_PAGE__DECISION_KEY_TAG = "authorizationDecisionKey";
   private static final String AUTHORIZE_PAGE__DECISION_ALLOW_TAG = "authorizationDecisionAllow";
   private static final String AUTHORIZE_PAGE__DECISION_DENY_TAG = "authorizationDecisionDeny";

   private static final String AUTHORIZE_PAGE__APPLICATION_NAME_TAG = "applicationName";
   private static final String AUTHORIZE_PAGE__APPLICATION_DESCRIPTION_TAG = "applicationDescription";
   private static final String AUTHORIZE_PAGE__APPLICATION_LOGO_URI_TAG = "applicationLogoUri";
   private static final String AUTHORIZE_PAGE__APPLICATION_WEB_URI_TAG = "applicationWebUri";
   private static final String AUTHORIZE_PAGE__LOGGED_IN_AS_TAG = "loggedInAs";

   private static final String AUTHORIZE_PAGE__HIDDEN_FIELDS_SECTION_TAG = "hiddenFieldsSection";
   private static final String AUTHORIZE_PAGE__PERMISSIONS_SECTION_TAG = "permissionsSection";
   //@formatter:on

   @Override
   public Class<OAuthAuthorizationData> getSupportedClass() {
      return OAuthAuthorizationData.class;
   }

   @Override
   public ViewModel asViewModel(OAuthAuthorizationData data) {
      ViewModel model = new ViewModel(AUTHORIZE_PAGE__TEMPLATE);
      model.param(AUTHORIZE_PAGE__LOGGED_IN_AS_TAG, asTemplateValue(data.getEndUserName()));
      model.param(AUTHORIZE_PAGE__REPLY_TO_TAG, asTemplateValue(data.getReplyTo()));
      model.param(AUTHORIZE_PAGE__APPLICATION_NAME_TAG, asTemplateValue(data.getApplicationName()));
      model.param(AUTHORIZE_PAGE__APPLICATION_DESCRIPTION_TAG, asTemplateValue(data.getApplicationDescription()));
      model.param(AUTHORIZE_PAGE__APPLICATION_LOGO_URI_TAG, asTemplateValue(data.getApplicationLogoUri()));
      model.param(AUTHORIZE_PAGE__APPLICATION_WEB_URI_TAG, asTemplateValue(data.getApplicationWebUri()));
      model.param(AUTHORIZE_PAGE__DECISION_KEY_TAG, AUTHORIZATION_DECISION_KEY);
      model.param(AUTHORIZE_PAGE__DECISION_ALLOW_TAG, AUTHORIZATION_DECISION_ALLOW);
      model.param(AUTHORIZE_PAGE__DECISION_DENY_TAG, AUTHORIZATION_DECISION_DENY);

      model.param(AUTHORIZE_PAGE__HIDDEN_FIELDS_SECTION_TAG,
         HiddenFormFields.newForm() //
            .add(OAuthConstants.CLIENT_AUDIENCE, data.getAudience()) //
            .add(OAuthConstants.SESSION_AUTHENTICITY_TOKEN, data.getAuthenticityToken())//
            .add(OAuthConstants.CLIENT_ID, data.getClientId()) //
            .add(OAuthConstants.SCOPE, data.getProposedScope())//
            .add(OAuthConstants.REDIRECT_URI, data.getRedirectUri()) //
            .add(OAuthConstants.STATE, data.getState())//
            .build());

      InputFields input = InputFields.newListGroupContainer();
      List<? extends Permission> permissions = data.getPermissions();
      if (permissions.isEmpty()) {
         Permission permission = new OAuthPermission("Full Data Access", "Application is able to read/write all data.");
         permission.setDefault(true);
         addItem(input, permission);
      } else {
         for (Permission permission : permissions) {
            addItem(input, permission);
         }
      }
      model.param(AUTHORIZE_PAGE__PERMISSIONS_SECTION_TAG, input.build());
      return model;
   }

   private void addItem(InputFields input, Permission perm) {
      String permissionName = perm.getPermission();
      String key = String.format("%s_status", permissionName);
      input.add(key, InputType.checkbox, permissionName, perm.getDescription(), "", "allow", perm.isDefault());
   }

}
