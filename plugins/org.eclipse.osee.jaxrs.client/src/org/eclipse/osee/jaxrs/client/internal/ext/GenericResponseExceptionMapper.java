/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.jaxrs.client.internal.ext;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.apache.cxf.jaxrs.client.ResponseExceptionMapper;
import org.eclipse.osee.jaxrs.client.JaxRsExceptions;

/**
 * @author Roberto E. Escobar
 */
@Provider
public class GenericResponseExceptionMapper implements ResponseExceptionMapper<Throwable> {

   @Override
   public Throwable fromResponse(Response response) {
      return JaxRsExceptions.asOseeException(response);
   }

}