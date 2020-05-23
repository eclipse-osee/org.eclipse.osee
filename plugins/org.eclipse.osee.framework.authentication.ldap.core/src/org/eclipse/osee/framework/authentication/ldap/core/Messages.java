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
package org.eclipse.osee.framework.authentication.ldap.core;

import org.eclipse.osgi.util.NLS;

/**
 * @author Swapna
 */
public class Messages extends NLS {

   /**
    * bundle name
    */
   private static final String BUNDLE_NAME = "org.eclipse.osee.framework.authentication.ldap.core.messages"; //$NON-NLS-1$
   /**
    * String to store LDAP service extension point ID
    */
   public static String LDAPServiceExtensionPointID;
   /**
    * String to store LDAP Service extension point attribute
    */
   public static String LDAPServiceExtensionPointAttribute;
   /**
    * String to store the LDAP Authentication failure
    */
   public static String LDAPAuthenticationFailed;
   /**
    * String to store the LDAP Authentication Protocol name
    */
   public static String LDAPAuthenticationProtocol;

   static {
      // initialize resource bundle
      NLS.initializeMessages(BUNDLE_NAME, Messages.class);
   }

   /**
    * Constructor
    */
   private Messages() {
      // Default constructor
   }
}
