/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.framework.jdk.core.util.io;

import java.io.OutputStream;
import java.util.function.Consumer;

/**
 * @author Ryan D. Brooks
 */
public class StringOutputStream extends OutputStream {

   private final StringBuilder strB;
   private final Consumer<String> onClose;

   public StringOutputStream() {
      this(null);
   }

   public StringOutputStream(Consumer<String> onClose) {
      this(onClose, 5000);
   }

   public StringOutputStream(Consumer<String> onClose, int initialCapacity) {
      strB = new StringBuilder(initialCapacity);
      this.onClose = onClose;
   }

   @Override
   public void write(int b) {
      strB.append((char) b);
   }

   @Override
   public void write(byte[] bytes, int offset, int length) {
      strB.append(new String(bytes, offset, length));
   }

   @Override
   public String toString() {
      return strB.toString();
   }

   @Override
   public void close() {
      if (onClose != null) {
         onClose.accept(toString());
      }
   }
}