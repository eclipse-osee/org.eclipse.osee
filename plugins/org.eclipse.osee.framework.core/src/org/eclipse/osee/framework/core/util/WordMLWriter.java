/*********************************************************************
 * Copyright (c) 2020 Boeing
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
package org.eclipse.osee.framework.core.util;

import java.io.IOException;
import java.io.Writer;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Branden W. Phillips
 */
public class WordMLWriter extends WordMLProducer {

   private final Writer writer;

   public WordMLWriter(Writer writer) {
      super(null);
      this.writer = writer;
   }

   @Override
   protected void append(CharSequence value) {
      try {
         writer.append(value);
      } catch (IOException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }

}
