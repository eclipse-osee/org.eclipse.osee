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

package org.eclipse.osee.framework.core.model;

import java.net.URL;
import org.eclipse.osee.framework.core.data.FullyNamedIdentity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Ryan D. Brooks
 */
public abstract class ResourceToken extends FullyNamedIdentity<Long> {

   public ResourceToken(Long uuid, String name) {
      super(uuid, name);
   }

   public abstract URL getUrl() throws OseeCoreException;
}