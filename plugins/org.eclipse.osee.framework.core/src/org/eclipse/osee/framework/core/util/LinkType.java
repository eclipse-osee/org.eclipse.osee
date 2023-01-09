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

package org.eclipse.osee.framework.core.util;

/**
 * @author Roberto E. Escobar
 * @author David W. Miller
 */
public enum LinkType {
   OSEE_SERVER_LINK,
   INTERNAL_DOC_REFERENCE_USE_NAME,
   INTERNAL_DOC_REFERENCE_USE_PARAGRAPH_NUMBER,
   INTERNAL_DOC_REFERENCE_USE_PARAGRAPH_NUMBER_AND_NAME;

   public boolean isArtifactNameRequired() {
      //@formatter:off
      return
            this == LinkType.OSEE_SERVER_LINK
         || this == LinkType.INTERNAL_DOC_REFERENCE_USE_NAME
         || this == LinkType.INTERNAL_DOC_REFERENCE_USE_PARAGRAPH_NUMBER_AND_NAME;
      //@formatter:on
   }

   public boolean isParagraphRequired() {
      //@formatter:off
      return
            this == LinkType.INTERNAL_DOC_REFERENCE_USE_PARAGRAPH_NUMBER
         || this == LinkType.INTERNAL_DOC_REFERENCE_USE_PARAGRAPH_NUMBER_AND_NAME;
      //@formatter:on
   }

}
