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
package org.eclipse.osee.orcs.utility;

import javax.ws.rs.core.HttpHeaders;

public class RestUtil {
   public static String getAccountId(HttpHeaders httpHeaders) {
      String clientId = httpHeaders.getHeaderString("osee.account.id");
      if (clientId == null) {
         clientId = "";
      }
      return clientId;
   }
}