/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.framework.jdk.core.type;

import java.net.URL;
import org.eclipse.osee.framework.jdk.core.util.Lib;

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
      this(uuid, name, clazz, Lib.getExtension(name) + "/");
   }

   public ClassBasedResourceToken(String name, Class<?> clazz) {
      this(-1L, name, clazz);
   }

   @Override
   public URL getUrl() {
      if (url == null) {
         url = clazz.getResource(path + getName());
         if (url == null) {
            throw new OseeArgumentException("Unable to resolve url for class [%s] with path [%s] and token [%s]", clazz,
               path, this);
         }
      }
      return url;
   }
}