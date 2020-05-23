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

package org.eclipse.osee.framework.skynet.core.importing.parsers;

import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughArtifactCollector;

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
   public void processContent(OperationLogger logger, RoughArtifactCollector collector, boolean forceBody, boolean forcePrimaryType, String headerNumber, String listIdentifier, String paragraphStyle, String content, boolean isParagraph) {
      //
   }

   @Override
   public void finish() {
      //
   }

}
