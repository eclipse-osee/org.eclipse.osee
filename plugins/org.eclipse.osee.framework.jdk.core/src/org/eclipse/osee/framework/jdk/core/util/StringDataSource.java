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
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;

/**
 * @author Ryan D. Brooks
 */
public class StringDataSource implements DataSource {

   private final String data;
   private final String name;

   public StringDataSource(String data, String name) {
      super();
      this.data = data;
      this.name = name;
   }

   @Override
   public String getContentType() {
      return "text/plain";
   }

   @Override
   public InputStream getInputStream() {
      return new ByteArrayInputStream(data.getBytes());
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
