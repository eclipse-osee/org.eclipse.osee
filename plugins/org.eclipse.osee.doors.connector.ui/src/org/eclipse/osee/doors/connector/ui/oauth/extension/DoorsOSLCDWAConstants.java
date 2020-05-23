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
package org.eclipse.osee.doors.connector.ui.oauth.extension;

/**
 * Class to provide the Constants
 *
 * @author Chandan Bandemutt
 */
public interface DoorsOSLCDWAConstants {

   /**
    *
    */
   final String AUTHORIZE_URL = "/oauth-authorize-token";
   /**
    *
    */
   final String REQUEST_TOKEN_URL = "/oauth-request-token";
   /**
    *
    */
   final String ACCESS_TOKEN_URL = "/oauth-access-token";

   /**
    *
    */
   final String AUTHENTICATION_LOGIN_URL = "/oauth/j_acegi_security_check";

   /**
    *
    */
   final String PROTECTED_RESOURCE_URL = "/rm/discovery/catalog";
}
