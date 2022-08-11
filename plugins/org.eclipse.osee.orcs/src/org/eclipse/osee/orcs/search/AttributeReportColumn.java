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

package org.eclipse.osee.orcs.search;

import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

/**
 * @author David W. Miller
 */
public class AttributeReportColumn extends ReportColumn {
   private final AttributeTypeToken type;

   public AttributeReportColumn(String name, AttributeTypeToken type) {
      super(name);
      this.type = type;
   }

   public AttributeReportColumn(AttributeTypeToken type) {
      super(type.getName());
      this.type = type;
   }

   @Override
   public String getReportData(ArtifactReadable artifact) {
      if (artifact == null) {
         return "";
      }
      return artifact.getAttributeValuesAsString(type);
   }

   public AttributeTypeToken getType() {
      return type;
   }

}
