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

package org.eclipse.osee.framework.skynet.core.attribute;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Roberto E. Escobar
 */
public final class CompressedContentAttribute extends BinaryAttribute<InputStream> {

   @Override
   public InputStream getValue() {
      return Lib.byteBufferToInputStream(getAttributeDataProvider().getValueAsBytes());
   }

   @Override
   protected boolean subClassSetValue(InputStream value) {
      return setValueFromInputStream(value);
   }

   @Override
   public boolean setValueFromInputStream(InputStream value) {
      boolean response = false;
      try {
         byte[] data = Lib.inputStreamToBytes(value);
         response = getAttributeDataProvider().setValue(ByteBuffer.wrap(data));
      } catch (IOException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
      if (response) {
         markAsNewOrChanged();
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
      getAttributeDataProvider().setDisplayableString(getAttributeType().getName());
   }
}
