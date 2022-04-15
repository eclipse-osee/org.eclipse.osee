/*********************************************************************
 * Copyright (c) 2022 Boeing
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.ws.rs.core.StreamingOutput;

/**
 * Implementation of the {@link StreamingOutput} interface that transfers data from a {@link InputStream}. This
 * implementation is not thread safe. Behavior is undefined when the {@link InputStream} is modified during a write
 * operation.
 *
 * @author Loren K. Ashley
 */

public class InputStreamStreamingOutput implements StreamingOutput {

   /**
    * Saves the {@link InputStream} that data is read from.
    */

   private final InputStream inputStream;

   /**
    * Creates a new {@link InputStreamStreamingOutput} object and saves the {@link InputStream} data is to be read from.
    *
    * @param inputStream The {@link InputStream} to read data from.
    */

   public InputStreamStreamingOutput(InputStream inputStream) {
      this.inputStream = inputStream;
   }

   /**
    * Reads byte data from the {@link InputStream} and sends it to the provided {@link OutputStream}. Each invocation of
    * this method will transfer the entire contents of the {@link InputStream} to the provided {@link OutputStream}.
    *
    * @param outputStream The {@link OutputStream} data from the {@link InputStream} will be written to.
    */

   @Override
   public void write(OutputStream outputStream) throws IOException {
      this.inputStream.transferTo(outputStream);
   }

}

/* EOF */
