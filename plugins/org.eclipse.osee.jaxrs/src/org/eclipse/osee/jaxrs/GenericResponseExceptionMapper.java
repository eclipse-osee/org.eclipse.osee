/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.jaxrs;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.apache.cxf.jaxrs.client.ResponseExceptionMapper;

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