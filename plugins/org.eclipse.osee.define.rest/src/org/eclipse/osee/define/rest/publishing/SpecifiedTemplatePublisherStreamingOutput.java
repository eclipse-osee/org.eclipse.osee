/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.define.rest.publishing;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.define.api.PublishingOptions;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Branden W. Phillips
 */
public class SpecifiedTemplatePublisherStreamingOutput implements StreamingOutput {

   private final PublishingOptions publishingOptions;
   private final ArtifactId templateArtId;
   private final ArtifactId headArtifact;
   private final OrcsApi orcsApi;
   private final Log logger;
   private Writer writer;

   public SpecifiedTemplatePublisherStreamingOutput(PublishingOptions publishingOptions, ArtifactId templateArtId, ArtifactId headArtifact, OrcsApi orcsApi, Log logger) {
      this.publishingOptions = publishingOptions;
      this.templateArtId = templateArtId;
      this.headArtifact = headArtifact;
      this.orcsApi = orcsApi;
      this.logger = logger;
   }

   @Override
   public void write(OutputStream opStream) {
      try (Writer writer = new OutputStreamWriter(opStream)) {
         MSWordSpecifiedTemplatePublisher publisher =
            new MSWordSpecifiedTemplatePublisher(publishingOptions, logger, orcsApi, writer);
         publisher.publish(templateArtId, headArtifact);
         writer.close();
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }
}
