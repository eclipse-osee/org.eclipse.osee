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
package org.eclipse.osee.orcs.core.internal.attribute.primitives;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.core.annotations.OseeAttribute;

@OseeAttribute("CompressedContentAttribute")
public final class CompressedContentAttribute extends BinaryAttribute<InputStream> {
   public static final String NAME = CompressedContentAttribute.class.getSimpleName();

   public CompressedContentAttribute(Long id) {
      super(id);
   }

   @Override
   public InputStream getValue() {
      return Lib.byteBufferToInputStream(getDataProxy().getValueAsBytes());
   }

   @Override
   public boolean subClassSetValue(InputStream value) {
      return setValueFromInputStream(value);
   }

   @Override
   public boolean setValueFromInputStream(InputStream value) {
      boolean response = false;
      try {
         if (value == null) {
            response = getDataProxy().setValue(null);
         } else {
            byte[] data = Lib.inputStreamToBytes(value);
            response = getDataProxy().setValue(ByteBuffer.wrap(data));
         }
      } catch (IOException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
      if (response) {
         markAsChanged(ModificationType.MODIFIED);
      }
      return response;
   }

   @Override
   public InputStream convertStringToValue(String value) {
      try {
         return Lib.stringToInputStream(value);
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   @Override
   protected void uponInitialize() {
      getDataProxy().setDisplayableString(getAttributeType().getName());
   }
}