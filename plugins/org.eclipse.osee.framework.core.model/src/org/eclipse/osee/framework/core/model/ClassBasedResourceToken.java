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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;

/**
 * @author Ryan D. Brooks
 */
public final class ClassBasedResourceToken extends ResourceToken {
   private URL url;
   private final Class<?> clazz;
   private final String path;

   public ClassBasedResourceToken(Long uuid, String name, Class<?> clazz, String path) {
      super(uuid, name);
      this.clazz = clazz;
      this.path = path;
   }

   public ClassBasedResourceToken(Long uuid, String name, Class<?> clazz) {
      this(uuid, name, clazz, "");
   }

   @Override
   public URL getUrl() throws OseeCoreException {
      if (url == null) {
         url = clazz.getResource(path + getName());
         Conditions.checkNotNull(url, "url", "Unable to resolve url for class [%s] with path [%s] and token [%s]",
            clazz, path, this);
      }
      return url;
   }
}