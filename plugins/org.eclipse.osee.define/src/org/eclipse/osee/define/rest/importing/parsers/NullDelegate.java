/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.define.rest.importing.parsers;

import org.eclipse.osee.define.api.importing.IArtifactExtractor;
import org.eclipse.osee.define.api.importing.IArtifactExtractorDelegate;
import org.eclipse.osee.define.api.importing.RoughArtifactCollector;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
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
   public XResultData processContent(OrcsApi orcsApi, XResultData results, RoughArtifactCollector collector, boolean forceBody, boolean forcePrimaryType, String headerNumber, String listIdentifier, String paragraphStyle, String content, boolean isParagraph) {
      return results;
   }

   @Override
   public void finish(OrcsApi orcsApi, XResultData results, RoughArtifactCollector collector) {
      //
   }

   @Override
   public void finish() {
      //
   }

}
