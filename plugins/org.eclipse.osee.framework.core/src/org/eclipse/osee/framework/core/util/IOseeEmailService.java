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

import java.util.Collection;
import org.eclipse.osee.framework.core.util.OseeEmail.BodyType;

/**
 * @author Donald G. Dunne
 */
public interface IOseeEmailService {

   /**
    * @param subject sanitized subject line; must not contain user-input titles or sensitive data. Abridged email subject
    * is auto-derived as "[Abridged] " + subject.
    * @param emailAddressesAbridged addresses to send abridged email with abridged body
    * @param bodyAbridged generic email body with no sensitive data
    */
   IOseeEmail create(Collection<String> toAddresses, String fromAddress, String replyToAddress, String subject,
      String body, BodyType bodyType, Collection<String> emailAddressesAbridged, String bodyAbridged);

   /**
    * @param subject sanitized subject line; must not contain user-input titles or sensitive data. Abridged email subject
    * is auto-derived as "[Abridged] " + subject.
    * @param emailAddressAbridged address to send abridged email with abridged body
    * @param bodyAbridged generic email body with no sensitive data
    */
   IOseeEmail create(String fromEmail, String toAddress, String subject, String body, BodyType bodyType,
      String emailAddressAbridged, String bodyAbridged);

   IOseeEmail create();

}
