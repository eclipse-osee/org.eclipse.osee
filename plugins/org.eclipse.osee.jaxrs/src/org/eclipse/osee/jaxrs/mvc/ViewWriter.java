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
package org.eclipse.osee.jaxrs.mvc;

import java.io.IOException;
import java.io.OutputStream;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

/**
 * @author Roberto E. Escobar
 */
public interface ViewWriter {

   MediaType getMediaType();

   void write(MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException;

}