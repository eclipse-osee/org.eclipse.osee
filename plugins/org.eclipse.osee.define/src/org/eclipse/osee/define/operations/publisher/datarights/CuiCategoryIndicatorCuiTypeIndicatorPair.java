/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.define.operations.publisher.datarights;

import java.util.Objects;
import org.eclipse.osee.framework.core.publishing.CuiCategoryIndicator;
import org.eclipse.osee.framework.core.publishing.CuiTypeIndicator;

/**
 * Saves a CUI Category and it's associated CUI Type.
 *
 * @author Loren K. Ashley
 */

public class CuiCategoryIndicatorCuiTypeIndicatorPair {

   /**
    * Saves the CUI Category.
    */

   CuiCategoryIndicator cuiCategoryIndicator;

   /**
    * Saves the CUI Type.
    */

   CuiTypeIndicator cuiTypeIndicator;

   /**
    * Creates a new empty {@link CuiCategoryIndicatorCuiTypeIndicatorPair} for JSON deserialization.
    */

   public CuiCategoryIndicatorCuiTypeIndicatorPair() {
      this.cuiCategoryIndicator = null;
      this.cuiTypeIndicator = null;
   }

   /**
    * Creates a new {@link CuiCategoryIndicatorCuiTypeIndicatorPair} object for JSON serialization.
    *
    * @param cuiCategoryIndicator the CUI Category.
    * @param cuiTypeIndicator the CUI Type.
    * @throws NullPointerException when either of the parameters <code>cuiCategoryIndicator</code> or
    * <code>cuiTypeIndicator</code> are <code>null</code>.
    */

   public CuiCategoryIndicatorCuiTypeIndicatorPair(CuiCategoryIndicator cuiCategoryIndicator, CuiTypeIndicator cuiTypeIndicator) {
      this.cuiCategoryIndicator = Objects.requireNonNull(cuiCategoryIndicator,
         "CuiCategoryIndicatorCuiTypeIndicatorPair::new, parameter \"cuiCategoryIndicaotr\" cannot be null.");
      this.cuiTypeIndicator = Objects.requireNonNull(cuiTypeIndicator,
         "CuiCategoryIndicatorCuiTypeIndicatorPair::new, parameter \"cuiTypeIndicator\" cannot be null.");
   }

   /**
    * Gets the {@link #cuiCategoryIndicator}.
    *
    * @return the cuiCategoryIndicator.
    * @throws IllegalStateException when the member {@link #cuiCategoryIndicator} has already been set.
    */

   public CuiCategoryIndicator getCuiCategoryIndicator() {
      if (Objects.isNull(this.cuiCategoryIndicator)) {
         throw new IllegalStateException(
            "CuiCategoryIndicatorCuiTypeIndicatorPair::getCuiCategoryIndicator, the member \"this.cuiCategoryIndicator\" has not yet been set.");
      }

      return this.cuiCategoryIndicator;
   }

   /**
    * Gets the {@link #cuiTypeIndicator}.
    *
    * @return the cuiTypeIndicator.
    * @throws IllegalStateException when the member {@link #cuiTypeIndicator} has already been set.
    */

   public CuiTypeIndicator getCuiTypeIndicator() {
      if (Objects.isNull(this.cuiTypeIndicator)) {
         throw new IllegalStateException(
            "CuiCategoryIndicatorCuiTypeIndicatorPair::getCuiTypeIndicator, the member \"this.cuiTypeIndicator\" has not yet been set.");
      }

      return this.cuiTypeIndicator;
   }

   /**
    * Predicate to determine the validity of the {@link CuiCategoryIndicatorCuiTypeIndicatorPair} object. The validity
    * is determined as follows:
    * <ul>
    * <li>The member {@link #cuiCategoryIndicator} must be non-<code>null</code>.</li>
    * <li>The member {@link #cuiTypeIndicator} must be non-<code>null</code>.</li>
    * </ul>
    *
    * @return <code>true</code>, when the {@link CuiCategoryIndicatorCuiTypeIndicatorPair} object is valid; otherwise,
    * <code>false</code>.
    */

   boolean isValid() {
      //@formatter:off
      return
            Objects.nonNull( this.cuiCategoryIndicator )
         && Objects.nonNull( this.cuiTypeIndicator     );
      //@formatter:on
   }

   /**
    * Sets the member {@link #cuiCategoryIndicator}.
    *
    * @param cuiCategoryIndicator the cuiCategoryIndicator to set.
    * @throws NullPointerException when the parameter <code>cuiCategoryIndicator</code> is <code>null</code>.
    * @throws IllegalStateException when the member {@link #cuiCategoryIndicator} has already been set.
    */

   public void setCuiCategoryIndicator(CuiCategoryIndicator cuiCategoryIndicator) {
      if (Objects.nonNull(this.cuiCategoryIndicator)) {
         throw new IllegalStateException(
            "CuiCategoryIndicatorCuiTypeIndicatorPair::setCuiCategoryIndicator, member \"this.cuiCategoryIndicator\" has already been set.");
      }

      this.cuiCategoryIndicator = Objects.requireNonNull(cuiCategoryIndicator,
         "CuiCategoryIndicatorCuiTypeIndicatorPair::setCuiCategoryIndicator, parameter \"cuiCategoryIndicator\" cannot be null.");
   }

   /**
    * Sets the member {@link #cuiTypeIndicator}.
    *
    * @param cuiTypeIndicator the cuiTypeIndicator to set.
    * @throws NullPointerException when the parameter <code>cuiTypeIndicator</code> is <code>null</code>.
    * @throws IllegalStateException when the member {@link #cuiTypeIndicator} has already been set.
    */

   public void setCuiTypeIndicator(CuiTypeIndicator cuiTypeIndicator) {
      if (Objects.nonNull(this.cuiTypeIndicator)) {
         throw new IllegalStateException(
            "CuiCategoryIndicatorCuiTypeIndicatorPair::setCuiTypeIndicator, member \"this.cuiTypeIndicator\" has already been set.");
      }

      this.cuiTypeIndicator = Objects.requireNonNull(cuiTypeIndicator,
         "CuiCategoryIndicatorCuiTypeIndicatorPair::setCuiTypeIndicator, parameter \"cuiTypeIndicator\" cannot be null.");
   }

}

/* EOF */
