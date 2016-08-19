/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal.writer;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwCollector;

/**
 * @author Donald G. Dunne
 */
public final class OrcsWriterStreamingOutput implements StreamingOutput {
   private final OrcsApi orcsApi;
   private final OwCollector collector;

   public OrcsWriterStreamingOutput(OrcsApi orcsApi, OwCollector collector) {
      this.orcsApi = orcsApi;
      this.collector = collector;
   }

   @Override
   public void write(OutputStream output) {
      try {
         Writer writer = new OutputStreamWriter(output);
         OrcsWriterWorkbookGenerator generator = new OrcsWriterWorkbookGenerator(collector, orcsApi);
         generator.runOperation(orcsApi, writer);
      } catch (Exception ex) {
         // do nothing
      }
   }
}
