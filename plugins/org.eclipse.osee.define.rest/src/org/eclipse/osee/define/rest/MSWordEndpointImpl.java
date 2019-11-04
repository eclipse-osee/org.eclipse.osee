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
package org.eclipse.osee.define.rest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.define.api.DefineApi;
import org.eclipse.osee.define.api.MSWordEndpoint;
import org.eclipse.osee.define.api.WordTemplateContentData;
import org.eclipse.osee.define.api.WordUpdateChange;
import org.eclipse.osee.define.api.WordUpdateData;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author David W. Miller
 */
public final class MSWordEndpointImpl implements MSWordEndpoint {

   private final DefineApi defineApi;

   public MSWordEndpointImpl(DefineApi defineApi) {
      this.defineApi = defineApi;
   }

   @Override
   public WordUpdateChange updateWordArtifacts(WordUpdateData data) {
      return defineApi.getMSWordOperations().updateWordArtifacts(data);
   }

   @Override
   public Pair<String, Set<String>> renderWordTemplateContent(WordTemplateContentData data) {
      return defineApi.getMSWordOperations().renderWordTemplateContent(data);
   }

   @Override
   public Response publishWithNestedTemplates(BranchId branch, ArtifactId masterTemplate, ArtifactId slaveTemplate, ArtifactId headArtifact) {
      StreamingOutput streamingOutput = defineApi.getMSWordOperations().publishWithNestedTemplates(branch,
         masterTemplate, slaveTemplate, headArtifact);

      //Get date/time for filename
      SimpleDateFormat format = new SimpleDateFormat("MM-dd_HH-mm-ss");
      Date date = new Date(System.currentTimeMillis());
      String time = format.format(date);

      String msofdName = headArtifact.getIdString();

      String fileName = msofdName + "_" + time + ".xml";

      ResponseBuilder builder = Response.ok(streamingOutput);
      builder.header("Content-Disposition", "attachment; filename=" + fileName);
      return builder.build();
   }

   @Override
   public String getDocumentNames(BranchId branchId) {
      return null;
   }
}
