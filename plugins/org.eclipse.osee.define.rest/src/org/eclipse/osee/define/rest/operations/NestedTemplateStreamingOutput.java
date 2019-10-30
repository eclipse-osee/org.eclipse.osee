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

package org.eclipse.osee.define.rest.operations;

import java.io.IOException;
import java.io.OutputStream;
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
public class NestedTemplateStreamingOutput implements StreamingOutput {

   private final PublishingOptions publishingOptions;
   private final ArtifactId masterTemplateArtId;
   private final ArtifactId slaveTemplateArtId;
   private final ArtifactId headArtifact;
   private final OrcsApi orcsApi;
   private final Log logger;
   private Writer writer;

   public NestedTemplateStreamingOutput(PublishingOptions publishingOptions, ArtifactId masterTemplateArtId, ArtifactId slaveTemplateArtId, ArtifactId headArtifact, OrcsApi orcsApi, Log logger) {
      this.publishingOptions = publishingOptions;
      this.masterTemplateArtId = masterTemplateArtId;
      this.slaveTemplateArtId = slaveTemplateArtId;
      this.headArtifact = headArtifact;
      this.orcsApi = orcsApi;
      this.logger = logger;
   }

   @Override
   public void write(OutputStream arg0) throws IOException, WebApplicationException {
      WordTemplateProcessor publisher = new WordTemplateProcessor(publishingOptions, logger, orcsApi);
      String publishOutput =
         publisher.publishWithNestedTemplates(masterTemplateArtId, slaveTemplateArtId, headArtifact);

      writer.write(publishOutput);
      writer.close();
   }

}
