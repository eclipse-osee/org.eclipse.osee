/*********************************************************************
 * Copyright (c) 2017 Boeing
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
 * Enumeration used with {@link RendererOption} to specify the type of value associated with each
 * {@link RendererOption}.
 *
 * @author Morgan E. Cook
 * @author Loren K. Ashley
 */

public enum OptionType {
   Artifact,
   ArtifactId(org.eclipse.osee.framework.core.data.ArtifactId.SENTINEL),
   ArtifactType,
   Boolean(false),
   BranchId(org.eclipse.osee.framework.core.data.BranchId.SENTINEL),
   LinkType,
   ProgressMonitor,
   String,
   Transaction,
   OutputStream;

   /**
    * Saves the default value for the {@link OptionType}.
    */

   private final Object defaultValue;

   /**
    * Creates a new {@link OptionType} member with a <code>null</code> default value.
    */

   private OptionType() {
      this.defaultValue = null;
   }

   /**
    * Creates a new {@link OptionType} member with the specified <code>defaultValue</code>.
    *
    * @param defaultValue the default value for the {@link OptionType} member.
    */

   private OptionType(Object defaultValue) {
      this.defaultValue = defaultValue;
   }

   /**
    * Gets the default value to be used when a map of {@link RendererOption} does not contain an entry for the specified
    * key.
    *
    * @return the default value for the {@link OptionType}.
    */

   public Object getDefaultValue() {
      return this.defaultValue;
   }
}

/* EOF */