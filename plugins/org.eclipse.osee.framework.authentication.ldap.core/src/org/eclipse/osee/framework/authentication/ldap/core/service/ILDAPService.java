/*********************************************************************
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.framework.authentication.ldap.core.service;

/**
 * The clients using LDAP authentication extension (org.eclipse.osee.framework.authentication.ldap.core.service) should
 * provide a class which implements this interface to configure the LDAP connectivity.
 *
 * @author Swapna
 */
public interface ILDAPService {

   /**
    * Getter for user name as in LDAP directory.
    *
    * @return LDAP user name
    */
   String getLDAPUsername();

   /**
    * Getter for password.
    *
    * @return LDAP password
    */
   String getLDAPCrendentials();

   /**
    * Getter for LDAP URL.
    *
    * @return LDAP server name
    */
   String getLDAPServerName();

   /**
    * Getter for LDAP port.
    *
    * @return LDAP port number
    */
   String getLDAPPort();

   /**
    * Getter for LDAP search base
    *
    * @return LDAP search base where user search needs to be performed
    */
   String getLDAPSearchBase();

}
