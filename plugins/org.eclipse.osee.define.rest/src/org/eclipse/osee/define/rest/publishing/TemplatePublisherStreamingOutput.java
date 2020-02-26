/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.define.rest.publishing;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.define.api.PublishingOptions;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Branden W. Phillips
 */
public class TemplatePublisherStreamingOutput implements StreamingOutput {

   private final PublishingOptions publishingOptions;
   private final ArtifactId templateArtId;
   private final ArtifactId headArtifact;
   private final OrcsApi orcsApi;
   private final Log logger;
   private Writer writer;

   public TemplatePublisherStreamingOutput(PublishingOptions publishingOptions, ArtifactId templateArtId, ArtifactId headArtifact, OrcsApi orcsApi, Log logger) {
      this.publishingOptions = publishingOptions;
      this.templateArtId = templateArtId;
      this.headArtifact = headArtifact;
      this.orcsApi = orcsApi;
      this.logger = logger;
   }

   @Override
   public void write(OutputStream arg0) throws IOException, WebApplicationException {
      MSWordTemplatePublisher publisher = new MSWordTemplatePublisher(publishingOptions, logger, orcsApi);
      writer = new OutputStreamWriter(arg0);
      writer.write("");
      String publishOutput = publisher.publish(templateArtId, headArtifact);
      writer.write(publishOutput);
      writer.close();
   }

}
