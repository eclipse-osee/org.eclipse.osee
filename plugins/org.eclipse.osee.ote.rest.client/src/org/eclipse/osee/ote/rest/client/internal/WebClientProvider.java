/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.rest.client.internal;

import java.net.URI;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import com.sun.jersey.api.client.AsyncWebResource;
import com.sun.jersey.api.client.WebResource;

/**
 * @author John Misinco
 * @author Roberto E. Escobar
 */
public interface WebClientProvider {

   WebResource createResource(URI uri) throws OseeCoreException;

   AsyncWebResource createAsyncResource(URI uri) throws OseeCoreException;

}