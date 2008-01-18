/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.skynet.core.linking;

import org.eclipse.osee.framework.jdk.core.util.OseeProperties;

/**
 * @author Roberto E. Escobar
 */
public class HttpDeleteMethod implements IHttpMethod {

   private static final String FILENAME_KEY = "filename";
   private HttpFileHandler fileHandler;

   protected HttpDeleteMethod() {
      String rootPath = OseeProperties.getInstance().getRemoteHttpServerUploadPath();
      fileHandler = new HttpFileHandler(rootPath);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.linking.IHttpMethod#processRequest(org.eclipse.osee.framework.skynet.core.linking.HttpRequest, org.eclipse.osee.framework.skynet.core.linking.HttpResponse)
    */
   public void processRequest(HttpRequest httpRequest, HttpResponse httpResponse) {
      // TODO: IMPLEMENT A DELETE METHOD
      System.out.println("Got a Delete Request");
   }

}
