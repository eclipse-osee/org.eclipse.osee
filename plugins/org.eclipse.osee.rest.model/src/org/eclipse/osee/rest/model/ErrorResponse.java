/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.rest.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Roberto E. Escobar
 */
@XmlRootElement
public class ErrorResponse {

   private int errorCode;
   private String errorType;
   private String errorReason;
   private String errorMessage;
   private String exception;

   public int getErrorCode() {
      return errorCode;
   }

   public void setErrorCode(int errorCode) {
      this.errorCode = errorCode;
   }

   public String getErrorType() {
      return errorType;
   }

   public void setErrorType(String errorType) {
      this.errorType = errorType;
   }

   public String getErrorReason() {
      return errorReason;
   }

   public void setErrorReason(String errorReason) {
      this.errorReason = errorReason;
   }

   public String getErrorMessage() {
      return errorMessage;
   }

   public void setErrorMessage(String errorMessage) {
      this.errorMessage = errorMessage;
   }

   public String getException() {
      return exception;
   }

   public void setException(String exception) {
      this.exception = exception;
   }

   @Override
   public String toString() {
      return "ErrorResponse [errorCode=" + errorCode + ", errorType=" + errorType + ", errorReason=" + errorReason + ", errorMessage=" + errorMessage + ", exception=" + exception + "]";
   }

}
