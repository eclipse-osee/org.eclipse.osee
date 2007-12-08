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
   public void processRequest(HttpRequest httpRequest, HttpResponse httpResponse);

}
