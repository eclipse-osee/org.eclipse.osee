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
import org.apache.cxf.rs.security.oauth2.common.OAuthError;
import org.eclipse.osee.framework.jdk.core.type.ViewModel;
import org.eclipse.osee.jaxrs.server.internal.resources.AbstractHtmlWriter;

/**
 * @author Roberto E. Escobar
 */
@Provider
public class OAuthErrorHtmlWriter extends AbstractHtmlWriter<OAuthError> {

   //@formatter:off
   private static final String ERROR_PAGE__TEMPLATE = "oauth_error.html";
   private static final String ERROR_PAGE__CODE_TAG = "errorCode";
   private static final String ERROR_PAGE__DESCRIPTION_TAG = "errorDescription";
   private static final String ERROR_PAGE__ERROR_URI = "errorUri";
   private static final String ERROR_PAGE__ERROR_STATE = "errorState";
   //@formatter:on

   @Override
   public Class<OAuthError> getSupportedClass() {
      return OAuthError.class;
   }

   @Override
   public ViewModel asViewModel(OAuthError data) {
      ViewModel model = new ViewModel(ERROR_PAGE__TEMPLATE);
      model.param(ERROR_PAGE__CODE_TAG, asTemplateValue(data.getError()));
      model.param(ERROR_PAGE__DESCRIPTION_TAG, asTemplateValue(data.getErrorDescription()));
      model.param(ERROR_PAGE__ERROR_URI, asTemplateValue(data.getErrorUri()));
      model.param(ERROR_PAGE__ERROR_STATE, asTemplateValue(data.getState()));
      return model;
   }
}
