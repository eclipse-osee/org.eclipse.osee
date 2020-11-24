/*********************************************************************
 * Copyright (c) 2020 Boeing
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
package org.eclipse.osee.define.api;

import java.util.Comparator;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.CoreActivityTypes;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Branden W. Phillips
 */
public class AttributeAlphabeticalComparator implements Comparator<ArtifactReadable> {

   private final ActivityLog activityLog;
   private final AttributeTypeToken attributeType;

   public AttributeAlphabeticalComparator(ActivityLog activityLog, AttributeTypeToken attributeType) {
      this.activityLog = activityLog;
      this.attributeType = attributeType;
   }

   @Override
   public int compare(ArtifactReadable art1, ArtifactReadable art2) {
      try {
         String attr1 = art1.getAttributeValuesAsString(attributeType);
         String attr2 = art2.getAttributeValuesAsString(attributeType);
         if (attr1 == null && attr2 == null) {
            return 0;
         } else if (attr1 == null) {
            return 1;
         } else if (attr2 == null) {
            return -1;
         } else {
            attr1 = attr1.toLowerCase();
            attr2 = attr2.toLowerCase();
            return attr1.compareTo(attr2);
         }
      } catch (Exception ex) {
         activityLog.createThrowableEntry(CoreActivityTypes.OSEE_ERROR, ex);
      }
      return 1;

   }
}
