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

package org.eclipse.osee.framework.core.publishing;

import java.io.OutputStream;
import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.util.LinkType;

/**
 * Enumeration used with {@link RendererOption} to specify the type of value associated with each
 * {@link RendererOption}.
 *
 * @author Morgan E. Cook
 * @author Loren K. Ashley
 */

public enum OptionType {
//@formatter:off
// +-----------------+-------------------------+--------------+---------+----------------------------------------------------------+
// | Option Type     | Class                   | isCollection | canCopy | defaultValue                                             |
// +-----------------+-------------------------+--------------+---------+----------------------------------------------------------+
     Artifact        ( ArtifactReadable.class,  false,          false,    null                                                     ),
// +-----------------+-------------------------+--------------+---------+----------------------------------------------------------+
     ArtifactId      ( ArtifactId.class,        false,          true,     org.eclipse.osee.framework.core.data.ArtifactId.SENTINEL ),
// +-----------------+-------------------------+--------------+---------+----------------------------------------------------------+
     ArtifactTypes   ( ArtifactTypeToken.class, true,           true,     null                                                     ),
// +-----------------+-------------------------+--------------+---------+----------------------------------------------------------+
     Boolean         ( Boolean.class,           false,          true,     false                                                    ),
// +-----------------+-------------------------+--------------+---------+----------------------------------------------------------+
     BranchId        ( BranchId.class,          false,          true,     org.eclipse.osee.framework.core.data.BranchId.SENTINEL   ),
// +-----------------+-------------------------+--------------+---------+----------------------------------------------------------+
     Integer         ( Integer.class,           false,          true,     null                                                     ),
// +-----------------+-------------------------+--------------+---------+----------------------------------------------------------+
     LinkType        ( LinkType.class,          false,          true,     null                                                     ),
// +-----------------+-------------------------+--------------+---------+----------------------------------------------------------+
     ProgressMonitor ( null,                    false,          false,    null                                                     ),
// +-----------------+-------------------------+--------------+---------+----------------------------------------------------------+
     String          ( String.class,            false,          true,     null                                                     ),
// +-----------------+-------------------------+--------------+---------+----------------------------------------------------------+
     Transaction     ( null,                    false,          true,     null                                                     ),
// +-----------------+-------------------------+--------------+---------+----------------------------------------------------------+
     OutputStream    ( OutputStream.class,      false,          false,    null                                                     );
// +-----------------+-------------------------+--------------+---------+----------------------------------------------------------+
//@formatter:on

   /**
    * Saves the default value for the {@link OptionType}.
    */

   private final Object defaultValue;

   /**
    * Saves the expected implementation class for objects being associated with a {@link RendererOption} of the
    * {@link OptionType}.
    */

   private final Class<?> implementationClass;

   /**
    * Flag to indicate the objects being associated with a {@link RendererOption} of the {@link OptionType} type are
    * {@link Collection}s or scalar values.
    */

   private final boolean isCollection;

   /**
    * Flag to indicate that {@link RendererOption}s of the {@link OptionType} and their associated values can be copied
    * from one {@link RendererMap} to another.
    */

   private final boolean canCopy;

   /**
    * Creates a new {@link OptionType} member with the specified <code>defaultValue</code>.
    *
    * @param implementationClass the expected {@link Class} of objects being associated with a {@link RendererOption}.
    * @param isCollection flag to indicate the value is expected to be a {@link Collection} of the expected class.
    * @param canCopy flag to indicate the value can be copied from one map to another.
    * @param defaultValue the default value for the {@link OptionType} member.
    */

   private OptionType(Class<?> implementationClass, boolean isCollection, boolean canCopy, Object defaultValue) {
      this.implementationClass = implementationClass;
      this.isCollection = isCollection;
      this.canCopy = canCopy;
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

   /**
    * Gets the expected implementation class of object associated with {@link RendererOption}s of the
    * {@link OptionType}.
    *
    * @return the expected implementation class.
    */

   public Class<?> getImplementationClass() {
      return this.implementationClass;
   }

   /**
    * Flag to indicate if {@link RendererOption}s of the {@link OptionType} can be copied from one {@link RendererMap}
    * to another.
    *
    * @return the can copy flag.
    */

   public boolean canCopy() {
      return this.canCopy;
   }

   /**
    * Flag to indicate the values of {@link RenderOption}s of the {@link OptionType} are expected to be
    * {@link Collection}s of the expected implementation class or a scalar value.
    *
    * @return <code>true</code>, when the value is expected to be a {@link Collection}; otherwise, <code>false</code>.
    */

   public boolean isCollection() {
      return this.isCollection;
   }
}

/* EOF */