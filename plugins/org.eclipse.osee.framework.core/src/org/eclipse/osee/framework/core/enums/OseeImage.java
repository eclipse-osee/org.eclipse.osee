/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.enums;

/**
 * @author Donald G. Dunne
 */
public abstract class OseeImage extends OseeEnum {

   public static final boolean ensureUnique = false;

   public OseeImage(Long typeId, Long ordinal, String filename) {
      /**
       * Want to be able to have images with same filename as image token improves readability, so don't validate
       * uniqueness
       */
      super(typeId, ordinal, filename, ensureUnique);
   }

   @Override
   public abstract Long getTypeId();

   abstract public String getPluginId();

   @Override
   public OseeEnum getDefault() {
      return null;
   }

}
