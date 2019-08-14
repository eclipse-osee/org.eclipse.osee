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
package org.eclipse.osee.define.rest.importing.parsers;

import java.util.Collection;
import org.eclipse.define.api.importing.IArtifactExtractor;
import org.eclipse.define.api.importing.IArtifactExtractorDelegate;
import org.eclipse.define.api.importing.RoughArtifactCollector;
import org.eclipse.define.api.importing.RoughRelation;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.orcs.OrcsApi;

public final class RequirementTraceTableDelegate implements IArtifactExtractorDelegate {
   RequirementTraceTableParser parser = null;
   XResultData results = new XResultData();
   private final String relTypeName;

   public RequirementTraceTableDelegate(String relTypeName) {
      this.relTypeName = relTypeName;
   }

   @Override
   public boolean isApplicable(IArtifactExtractor parser) {
      return false;
   }

   @Override
   public void initialize() {
      parser = new RequirementTraceTableParser(2, results);
   }

   @Override
   public void dispose() {
      //
   }

   @Override
   public String getName() {
      return "Requirement Trace Table Delegate";
   }

   @Override
   public XResultData processContent(OrcsApi orcsApi, XResultData results, RoughArtifactCollector collector, boolean forceBody, boolean forcePrimaryType, String headerNumber, String listIdentifier, String paragraphStyle, String content, boolean isParagraph) {
      parser.handleAppendixATable(content);
      Collection<Pair<String, String>> output = parser.getTraces();
      if (output.size() > 0) {
         for (Pair<String, String> item : output) {
            collector.addRoughRelation(new RoughRelation(relTypeName, item.getFirst(), item.getSecond(),
               String.format("Add %s relation", relTypeName)));
         }
      }
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
