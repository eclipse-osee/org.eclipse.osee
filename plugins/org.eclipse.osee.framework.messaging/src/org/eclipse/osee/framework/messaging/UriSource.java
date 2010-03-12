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
package org.eclipse.osee.framework.messaging;

import java.io.Serializable;
import java.net.URI;

/**
 * @author Andrew M. Finkbeiner
 */
public class UriSource implements Source, Serializable {

   private static final long serialVersionUID = -917397242786038197L;
   private final URI source;

   public UriSource(URI source) {
      this.source = source;
   }

   public URI getSource() {
      return source;
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof UriSource)) {
         return false;
      }
      return source.equals(((UriSource) obj).source);
   }

   @Override
   public int hashCode() {
      return source.hashCode();
   }
}
