/*******************************************************************************
 * Copyright (c) 2017 Boeing.
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
import org.eclipse.osee.define.api.MSWordOperations;
import org.eclipse.osee.define.api.WordTemplateContentData;
import org.eclipse.osee.define.api.WordUpdateChange;
import org.eclipse.osee.define.api.WordUpdateData;
import org.eclipse.osee.define.rest.internal.wordupdate.WordTemplateContentRendererHandler;
import org.eclipse.osee.define.rest.internal.wordupdate.WordUpdateArtifact;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.osgi.service.event.EventAdmin;

public class MSWordOperationsImpl implements MSWordOperations {

   private final OrcsApi orcsApi;
   private final Log logger;
   private final EventAdmin eventAdmin;

   public MSWordOperationsImpl(OrcsApi orcsApi, Log logger, EventAdmin eventAdmin) {
      this.orcsApi = orcsApi;
      this.logger = logger;
      this.eventAdmin = eventAdmin;
   }

   @Override
   public Pair<String, Set<String>> renderWordTemplateContent(WordTemplateContentData data) {
      WordTemplateContentRendererHandler wordRendererHandler = new WordTemplateContentRendererHandler(orcsApi, logger);

      return wordRendererHandler.renderWordML(data);
   }

   @Override
   public WordUpdateChange updateWordArtifacts(WordUpdateData data) {
      WordUpdateArtifact updateArt = new WordUpdateArtifact(logger, orcsApi, eventAdmin);
      return updateArt.updateArtifacts(data);
   }
}
