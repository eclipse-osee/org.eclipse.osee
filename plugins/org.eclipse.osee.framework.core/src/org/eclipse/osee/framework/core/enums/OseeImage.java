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

   private static final Long ENUM_ID = 3241857093L;

   public OseeImage(String filename) {
      super(ENUM_ID, ENUM_ID + new Long(filename.hashCode()), filename);
   }

   @Override
   public Long getTypeId() {
      return ENUM_ID;
   }

   abstract public String getPluginId();

   @Override
   public OseeEnum getDefault() {
      return null;
   }

}
