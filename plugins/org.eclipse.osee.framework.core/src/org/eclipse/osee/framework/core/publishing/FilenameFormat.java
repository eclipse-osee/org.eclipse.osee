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

package org.eclipse.osee.framework.core.publishing;

/**
 * Enumeration of publishing output filename formats.
 * <ul>
 * <li>{@link #EXPORT}</li>
 * <li>{@link #PREVIEW}</li>
 * </ul>
 */

public enum FilenameFormat {

   /**
    * Files will be named using the artifact's full name.
    */

   EXPORT,

   /**
    * Files will be named according to the preview conventions.
    */

   PREVIEW;

   /**
    * Predicate to determine if the enumeration member is {@link #EXPORT}.
    *
    * @return <code>true</code> when the member is {@link #EXPORT}; otherwise, <code>false</code>.
    */

   public boolean isExport() {
      return this == EXPORT;
   }

   /**
    * Predicate to determine if the enumeration member is {@link #PREVIEW}.
    *
    * @return <code>true</code> when the member is {@link #PREVIEW}; otherwise, <code>false</code>.
    */

   public boolean isPreview() {
      return this == PREVIEW;
   }

}

/* EOF */
