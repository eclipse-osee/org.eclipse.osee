/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.framework.ui.skynet.render.word;

import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.PageOrientation;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author John Misinco
 */
public class WordRendererUtil {

   private WordRendererUtil() {
      // Utility Class
   }

   public static PageOrientation getPageOrientation(Artifact artifact) {
      String pageTypeValue = null;
      if (artifact.isAttributeTypeValid(CoreAttributeTypes.PageOrientation)) {
         pageTypeValue = artifact.getSoleAttributeValue(CoreAttributeTypes.PageOrientation, "Portrait");
      }
      return PageOrientation.fromString(pageTypeValue);
   }
}
