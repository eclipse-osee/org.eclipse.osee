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
package org.eclipse.osee.define.report;

import java.util.Set;
import org.eclipse.osee.define.report.api.WordTemplateContentData;
import org.eclipse.osee.define.report.api.WordUpdateChange;
import org.eclipse.osee.define.report.api.WordUpdateData;
import org.eclipse.osee.define.report.api.WordUpdateEndpoint;
import org.eclipse.osee.define.report.internal.wordupdate.WordTemplateContentRendererHandler;
import org.eclipse.osee.define.report.internal.wordupdate.WordUpdateArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.osgi.service.event.EventAdmin;

/**
 * @author David W. Miller
 */
public final class WordUpdateEndpointImpl implements WordUpdateEndpoint {

   private final OrcsApi orcsApi;
   private final Log logger;
   private final EventAdmin eventAdmin;

   public WordUpdateEndpointImpl(Log logger, OrcsApi orcsApi, EventAdmin eventAdmin) {
      this.orcsApi = orcsApi;
      this.logger = logger;
      this.eventAdmin = eventAdmin;
   }

   @Override
   public WordUpdateChange updateWordArtifacts(WordUpdateData data) {
      WordUpdateArtifact updateArt = new WordUpdateArtifact(logger, orcsApi, eventAdmin);
      validate(data);
      return updateArt.updateArtifacts(data);
   }

   protected void validate(WordUpdateData data) {
      Conditions.checkNotNull(data, "WordUpdateData");
      Conditions.checkNotNullOrEmpty(data.getComment(), "WordUpdateData comment");
      Conditions.checkNotNullOrEmpty(data.getArtifacts(), "WordUpdateData artifacts");
      Conditions.checkExpressionFailOnTrue(data.getUserArtId() <= 0, "WordUpdateData invalid user id %d",
         data.getUserArtId());
      Conditions.checkExpressionFailOnTrue(data.getBranch().isInvalid(), "WordUpdateData invalid branch %s",
         data.getBranch());
      Conditions.checkNotNull(data.getWordData(), "WordUpdateData content");
   }

   @Override
   public Pair<String, Set<String>> renderWordTemplateContent(WordTemplateContentData data) throws OseeCoreException {
      WordTemplateContentRendererHandler wordRendererHandler = new WordTemplateContentRendererHandler(orcsApi, logger);
      return wordRendererHandler.renderWordML(data);
   }
}
