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

import java.util.Set;
import javax.ws.rs.core.Response;
import org.eclipse.osee.define.api.DefineApi;
import org.eclipse.osee.define.api.RenderEndpoint;
import org.eclipse.osee.define.api.WordTemplateContentData;
import org.eclipse.osee.define.api.WordUpdateChange;
import org.eclipse.osee.define.api.WordUpdateData;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author David W. Miller
 */
public final class RenderEndpointImpl implements RenderEndpoint {

   private final DefineApi defineApi;

   public RenderEndpointImpl(DefineApi defineApi) {
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
   public Response msWordTemplatePublish(BranchId branch, ArtifactId template, ArtifactId headArtifact) {
      return defineApi.getMSWordOperations().msWordTemplatePublish(branch, template, headArtifact);
   }

   @Override
   public Response goalWorkflowPublish(ArtifactId template, ArtifactId goal) {
      return null;
   }

   @Override
   public String getDocumentNames(BranchId branchId) {
      return null;
   }
}
