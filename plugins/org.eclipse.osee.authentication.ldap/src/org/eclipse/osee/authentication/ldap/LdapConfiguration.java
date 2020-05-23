/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.authentication.ldap;

/**
 * @author Roberto E. Escobar
 */
public interface LdapConfiguration extends LdapAccountFilter, LdapGroupFilter {

   LdapAuthenticationType getAuthenticationType();

   LdapCredentialsSource getCredentialsSource();

   String getPassword();

   long getReadTimeoutInMillis();

   LdapReferralHandlingType getReferralHandlingType();

   String getServerAddress();

   String getUsername();

   boolean isSslVerifyEnabled();

   boolean isUserNameToLowerCaseEnabled();

   String getUserNameVariableName();

   String getGroupNamespace();

}