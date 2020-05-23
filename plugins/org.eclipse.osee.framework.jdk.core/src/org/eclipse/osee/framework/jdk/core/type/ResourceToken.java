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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author Ryan D. Brooks
 */
public abstract class ResourceToken extends NamedIdentity<Long> {

   public ResourceToken(Long uuid, String name) {
      super(uuid, name);
   }

   public abstract URL getUrl();

   public InputStream getInputStream() {
      try {
         return getUrl().openStream();
      } catch (IOException ex) {
         throw new OseeCoreException(ex);
      }
   }
}