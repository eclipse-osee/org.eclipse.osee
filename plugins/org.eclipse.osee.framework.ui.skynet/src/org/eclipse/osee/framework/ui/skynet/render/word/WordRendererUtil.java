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
      if (artifact.isAttributeTypeValid(CoreAttributeTypes.PageType)) {
         pageTypeValue = artifact.getSoleAttributeValue(CoreAttributeTypes.PageType, "Portrait");
      }
      return PageOrientation.fromString(pageTypeValue);
   }
}
