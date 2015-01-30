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
package org.eclipse.osee.disposition.model;

/**
 * @author Angel Avila
 */
public enum CopySetParamOption {
   NONE(0),
   OVERRIDE(1),
   OVERRIDE_EMPTY(2),
   MERGE(3);

   private final int value;

   CopySetParamOption(int value) {
      this.value = value;
   }

   public final int getValue() {
      return value;
   }

   public boolean isOverride() {
      return this == CopySetParamOption.OVERRIDE;
   }

   public boolean isEmptyOverride() {
      return this == CopySetParamOption.OVERRIDE_EMPTY;
   }

   public boolean isMerge() {
      return this == CopySetParamOption.MERGE;
   }

   public boolean isNone() {
      return this == CopySetParamOption.NONE;
   }
}