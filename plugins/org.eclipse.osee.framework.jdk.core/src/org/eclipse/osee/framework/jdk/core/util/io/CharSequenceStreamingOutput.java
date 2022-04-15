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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import javax.ws.rs.core.StreamingOutput;

/**
 * Implementation of the {@link StreamingOutput} interface that reads data from a {@link CharSequence}. This
 * implementation is not thread safe. Behavior is undefined when the {@link CharSequence} is modified during a write
 * operation.
 *
 * @author Loren K. Ashley
 */

public class CharSequenceStreamingOutput implements StreamingOutput {

   /**
    * Saves the {@link CharSequence} that data is read from.
    */

   private final CharSequence charSequence;

   /**
    * Creates a new {@link CharSequenceStreamingOutput} object and saves the {@link CharSequence} data is to be read
    * from.
    *
    * @param charSequence The {@link CharSequence} to read data from.
    */

   public CharSequenceStreamingOutput(CharSequence charSequence) {
      this.charSequence = charSequence;
   }

   /**
    * Reads byte data from the {@link CharSequence} and sends it to the provided {@link OutputStream}. Characters are
    * encoded into bytes according to the default system character encoding. Each invocation of this method will
    * transfer the entire contents of the {@link CharSequence} to the provided {@link OutputStream}.
    *
    * @param outputStream The {@link OutputStream} data from the {@link CharSequence} will be written to.
    */

   @Override
   public void write(OutputStream outputStream) throws IOException {

      OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);

      outputStreamWriter.append(this.charSequence).flush();
   }

}

/* EOF */
