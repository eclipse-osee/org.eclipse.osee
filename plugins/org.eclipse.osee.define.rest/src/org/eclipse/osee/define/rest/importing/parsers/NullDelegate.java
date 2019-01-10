/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.rest.importing.parsers;

import org.eclipse.define.api.importing.IArtifactExtractor;
import org.eclipse.define.api.importing.IArtifactExtractorDelegate;
import org.eclipse.define.api.importing.RoughArtifactCollector;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.orcs.OrcsApi;

public final class NullDelegate implements IArtifactExtractorDelegate {

   @Override
   public boolean isApplicable(IArtifactExtractor parser) {
      return false;
   }

   @Override
   public void initialize() {
      //
   }

   @Override
   public void dispose() {
      //
   }

   @Override
   public String getName() {
      return "Null Delegate";
   }

   @Override
   public void processContent(OrcsApi orcsApi, ActivityLog activityLog, RoughArtifactCollector collector, boolean forceBody, boolean forcePrimaryType, String headerNumber, String listIdentifier, String paragraphStyle, String content, boolean isParagraph) {
      //
   }

   @Override
   public void finish(OrcsApi orcsApi, ActivityLog activityLog, RoughArtifactCollector collector) {
      //
   }

   @Override
   public void finish() {
      //
   }

}
