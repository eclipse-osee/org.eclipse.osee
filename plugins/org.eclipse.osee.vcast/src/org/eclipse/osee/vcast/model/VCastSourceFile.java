/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.vcast.model;

/**
 * @author Shawn F. Cook
 */
public class VCastSourceFile {

   private final int id;
   private final String path;
   private final String displayName;
   private final int checksum;
   private final String displayPath;

   public VCastSourceFile(int id, String path, String displayName, int checksum, String displayPath) {
      this.id = id;
      this.path = path;
      this.displayName = displayName;
      this.checksum = checksum;
      this.displayPath = displayPath;
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

}
