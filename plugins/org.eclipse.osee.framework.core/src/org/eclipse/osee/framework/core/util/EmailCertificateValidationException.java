/*********************************************************************
 * Copyright (c) 2026 Boeing
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
package org.eclipse.osee.framework.core.util;

public class EmailCertificateValidationException extends RuntimeException {

   private static final long serialVersionUID = 1L;

   public EmailCertificateValidationException(String message) {
      super(message);
   }

   public EmailCertificateValidationException(String message, Throwable cause) {
      super(message, cause);
   }
}
