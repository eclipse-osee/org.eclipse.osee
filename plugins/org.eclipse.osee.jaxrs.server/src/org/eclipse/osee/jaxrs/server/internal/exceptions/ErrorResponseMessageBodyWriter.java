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
package org.eclipse.osee.jaxrs.server.internal.exceptions;

import static org.eclipse.osee.jaxrs.server.internal.JaxRsUtils.asTemplateValue;
import javax.ws.rs.ext.Provider;
import org.eclipse.osee.framework.jdk.core.type.ViewModel;
import org.eclipse.osee.jaxrs.ErrorResponse;
import org.eclipse.osee.jaxrs.server.internal.resources.AbstractHtmlWriter;

/**
 * @author Roberto E. Escobar
 */
@Provider
public class ErrorResponseMessageBodyWriter extends AbstractHtmlWriter<ErrorResponse> {

   private static final String ERROR_PAGE__TEMPLATE = "error_template.html";
   private static final String ERROR_PAGE__MESSAGE_TAG = "errorMessage";
   private static final String ERROR_PAGE__CODE_TAG = "errorStatusCode";
   private static final String ERROR_PAGE__REASON_TAG = "errorReason";
   private static final String ERROR_PAGE__TYPE_TAG = "errorType";
   private static final String ERROR_PAGE__EXCEPTION_TAG = "errorException";

   @Override
   public Class<ErrorResponse> getSupportedClass() {
      return ErrorResponse.class;
   }

   @Override
   public ViewModel asViewModel(ErrorResponse data) {
      ViewModel model = new ViewModel(ERROR_PAGE__TEMPLATE);
      model.param(ERROR_PAGE__MESSAGE_TAG, asTemplateValue(data.getErrorMessage()));
      model.param(ERROR_PAGE__CODE_TAG, asTemplateValue(String.valueOf(data.getErrorCode())));
      model.param(ERROR_PAGE__REASON_TAG, asTemplateValue(data.getErrorReason()));
      model.param(ERROR_PAGE__TYPE_TAG, asTemplateValue(data.getErrorType()));
      model.param(ERROR_PAGE__EXCEPTION_TAG, asTemplateValue(data.getException()));
      return model;
   }

}
