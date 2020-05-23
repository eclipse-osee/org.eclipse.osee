/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.mail.api.internal;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import javax.activation.DataSource;

/**
 * @author Roberto E. Escobar
 */
public class UrlDataSource implements DataSource {

   private final String name;
   private final URL url;
   private final String contentType;

   public UrlDataSource(String name, URL url, String contentType) {
      super();
      this.name = name;
      this.url = url;
      this.contentType = contentType;
   }

   @Override
   public String getContentType() {
      return contentType;
   }

   @Override
   public InputStream getInputStream() throws IOException {
      return new BufferedInputStream(url.openStream());
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public OutputStream getOutputStream() {
      throw new UnsupportedOperationException();
   }
}
