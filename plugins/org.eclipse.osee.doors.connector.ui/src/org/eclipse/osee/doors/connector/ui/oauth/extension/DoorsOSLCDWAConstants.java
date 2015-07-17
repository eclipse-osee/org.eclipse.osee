/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
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
