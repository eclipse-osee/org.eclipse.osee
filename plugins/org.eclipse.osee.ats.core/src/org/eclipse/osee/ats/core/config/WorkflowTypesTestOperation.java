/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.ats.core.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * Tests run to ensure types used in workflows are used properly
 *
 * @author Donald G. Dunne
 */
public class WorkflowTypesTestOperation {

   List<AttributeTypeToken> attrTypeHintChecked = new ArrayList<>();
   Map<String, AttributeTypeToken> typeDisplayNameToToken = new HashMap<>();
   XResultData rd = new XResultData();
   // ignoreTypes only exists until duplicate are removed.  New types should not be added to this list.
   List<AttributeTypeToken> ignoreTypes = Collections.asList(CoreAttributeTypes.Description);

   /**
    * Duplicate workflow types should not exist. Workflows across different teams and programs should re-use the same
    * attribute types to reduce confusion on which attribute types to use when reporting and viewing.
    */
   public XResultData run() {
      testArtType(AtsArtifactTypes.AbstractWorkflowArtifact);
      return rd;
   }

   private void testArtType(ArtifactTypeToken artType) {

      for (AttributeTypeToken attrType : artType.getValidAttributeTypes()) {
         if (attrTypeHintChecked.contains(attrType) || ignoreTypes.contains(attrType)) {
            continue;
         }

         // Check for duplicate Display Names.  Should use same common attr type instead of creating a new one
         String unqualName = attrType.getUnqualifiedName();
         if (typeDisplayNameToToken.containsKey(unqualName)) {
            AttributeTypeToken otherAttrType = typeDisplayNameToToken.get(unqualName);
            rd.errorf("Workflow Attr Type %s on Art Type %s has same unqualified name as %s\n",
               attrType.toStringWithId(), artType.toStringWithId(), otherAttrType.toStringWithId());
         } else {
            typeDisplayNameToToken.put(unqualName, attrType);
         }

         attrTypeHintChecked.add(attrType);
      }
      for (ArtifactTypeToken childArtType : artType.getAllDescendantTypes()) {
         testArtType(childArtType);
      }

   }
}
