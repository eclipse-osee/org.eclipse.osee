/**
 * <copyright> Copyright (c) Robert Bosch Engineering and Business Solutions Ltd India.All rights reserved. This program
 * and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html </copyright>
 */
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
