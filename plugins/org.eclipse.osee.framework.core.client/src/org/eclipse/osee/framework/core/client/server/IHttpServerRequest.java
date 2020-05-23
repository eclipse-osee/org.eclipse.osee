/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.core.client.server;

/**
 * @author Donald G. Dunne
 */
public interface IHttpServerRequest extends IHttpMethod {

   /**
    * @return single word request type for use in URL and in determining requestor
    */
   public String getRequestType();

   /**
    * @param httpRequest The HttpRequest object
    * @param httpResponse The HttpResponse object
    */
   @Override
   public void processRequest(HttpRequest httpRequest, HttpResponse httpResponse);

}
