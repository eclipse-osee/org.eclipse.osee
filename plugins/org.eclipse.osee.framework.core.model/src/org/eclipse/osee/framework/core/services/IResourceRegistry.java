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
package org.eclipse.osee.framework.core.services;

import java.io.InputStream;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.ResourceToken;

/**
 * @author Ryan D. Brooks
 */
public interface IResourceRegistry {

   ResourceToken registerResource(Long universalId, ResourceToken token) throws OseeCoreException;

   ResourceToken getResourceToken(Long universalId) throws OseeCoreException;

   InputStream getResource(Long universalId) throws Exception;

   void registerAll(Iterable<ResourceToken> tokens);

}