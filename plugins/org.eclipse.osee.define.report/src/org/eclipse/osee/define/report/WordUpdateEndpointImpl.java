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

import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.define.report.api.WordUpdateChange;
import org.eclipse.osee.define.report.api.WordUpdateData;
import org.eclipse.osee.define.report.api.WordUpdateEndpoint;
import org.eclipse.osee.define.report.internal.wordupdate.WordUpdateArtifact;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author David W. Miller
 */
public final class WordUpdateEndpointImpl implements WordUpdateEndpoint {

   private final OrcsApi orcsApi;
   private final IAtsServer atsServer;
   private final Log logger;

   public WordUpdateEndpointImpl(Log logger, OrcsApi orcsApi, IAtsServer atsServer) {
      this.orcsApi = orcsApi;
      this.atsServer = atsServer;
      this.logger = logger;
   }

   @Override
   public WordUpdateChange updateWordArtifacts(WordUpdateData data) {
      WordUpdateArtifact updateArt = new WordUpdateArtifact(logger, orcsApi);
      validate(data);
      return updateArt.updateArtifacts(data, atsServer);
   }

   private void validate(WordUpdateData data) {
      Conditions.checkNotNull(data, "WordUpdateData");
      Conditions.checkNotNull(data.getComment(), "WordUpdateData comment");
      Conditions.checkNotNullOrEmpty(data.getArtifacts(), "WordUpdateData artifacts");
      Conditions.checkExpressionFailOnTrue(data.getUserArtId() <= 0, "WordUpdateData invalid user id %d",
         data.getUserArtId());
      Conditions.checkExpressionFailOnTrue(data.getBranch() <= 0, "WordUpdateData invalid branch %d", data.getBranch());
      Conditions.checkNotNull(data.getWordData(), "WordUpdateData content");
   }

}
