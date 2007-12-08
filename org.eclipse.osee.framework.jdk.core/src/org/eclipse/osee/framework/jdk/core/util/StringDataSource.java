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
package org.eclipse.osee.framework.jdk.core.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;

public class StringDataSource implements DataSource {

   private String data;
   private String name;

   /**
    * @param data
    * @param name
    */
   public StringDataSource(String data, String name) {
      super();
      this.data = data;
      this.name = name;
   }

   public String getContentType() {
      return "text/plain";
   }

   public InputStream getInputStream() throws IOException {
      return new ByteArrayInputStream(data.getBytes());
   }

   public String getName() {
      return name;
   }

   public OutputStream getOutputStream() throws IOException {
      throw new UnsupportedOperationException();
   }

}
