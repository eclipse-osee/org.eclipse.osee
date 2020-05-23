/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.vcast.model;

/**
 * @author Shawn F. Cook
 */
public class VCastSourceFileJoin {

   private final int id;
   private final int unit_index;
   private final String path;
   private final String displayName;
   private final int checksum;
   private final String displayPath;

   public VCastSourceFileJoin(int id, String path, String displayName, int checksum, String displayPath, int unitIndex) {
      this.id = id;
      this.path = path;
      this.displayName = displayName;
      this.checksum = checksum;
      this.displayPath = displayPath;
      this.unit_index = unitIndex;
   }

   public int getId() {
      return id;
   }

   public String getPath() {
      return path;
   }

   public String getDisplayName() {
      return displayName;
   }

   public int getChecksum() {
      return checksum;
   }

   public String getDisplayPath() {
      return displayPath;
   }

   public int getUnitIndex() {
      return unit_index;
   }

}
