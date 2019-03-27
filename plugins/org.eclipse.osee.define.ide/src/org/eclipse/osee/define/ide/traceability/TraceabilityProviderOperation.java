/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.ide.traceability;

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author John R. Misinco
 */
public abstract class TraceabilityProviderOperation extends AbstractOperation implements ITraceabilityProvider {

   protected final List<String> QUAL_METHOD_TO_INSPECTION = Arrays.asList("Inspection", "Analysis");

   public TraceabilityProviderOperation(String operationName, String pluginId) {
      super(operationName, pluginId);
   }

   protected String getInspectionQual(Artifact req) {
      List<String> qualMethod = req.getAttributeValues(CoreAttributeTypes.QualificationMethod);
      if (qualMethod.contains("Inspection") || qualMethod.contains("Analysis")) {
         return "INSPECTION";
      } else if (qualMethod.contains("Special Qualification") || qualMethod.contains("Demonstration")) {
         return "MANUAL";
      }
      return "";
   }

}
