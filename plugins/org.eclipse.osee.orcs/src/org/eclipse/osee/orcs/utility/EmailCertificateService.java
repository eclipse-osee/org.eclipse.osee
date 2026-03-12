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
package org.eclipse.osee.orcs.utility;

public interface EmailCertificateService {

   /**
    * Parse, validate and store the current user's email public certificate.
    *
    * @param certificatePem PEM-encoded X.509 certificate string
    * @throws EmailCertificateValidationException if certificate is invalid or not suitable for email
    */
   void setPublicCertificateForCurrentUser(String certificatePem);

   /**
    * Retrieve the current user's email public certificate (PEM).
    *
    * @return PEM string, or null if none is stored
    */
   String getPublicCertificateForCurrentUser();

   /**
    * Delete the current user's email public certificate (if present).
    */
   void deletePublicCertificateForCurrentUser();
}
