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
package org.eclipse.osee.framework.skynet.core.attribute;

import java.io.IOException;
import java.io.InputStream;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Jeff C. Phillips
 */
public class VarcharMediaResolver implements IMediaResolver {

   protected String varchar;

   public VarcharMediaResolver() {
      super();
      this.varchar = null;
   }

   public byte[] getValue() {
      if (varchar == null) {
         return null;
      }
      return varchar.getBytes();
   }

   public boolean setValue(InputStream stream) {
      try {
         String value = Lib.inputStreamToString(stream);

         // Check the incoming value before modifying internal data
         if (value.length() > 4000) throw new IllegalArgumentException(
               "This resolver can not support over 4000 characters, given stream with " + value.length());

         if (value.equals(varchar)) return false;

         varchar = value;
         return true;
      } catch (IOException ex) {
         throw new RuntimeException("this should never happen.", ex);
      }
   }

   public void setVarchar(String varchar) {
      this.varchar = varchar;
   }

   public void setBlobData(InputStream stream) {
   }

   public byte[] getBlobData() {
      return null;
   }

   public String getvarchar() {
      return varchar;
   }

}
