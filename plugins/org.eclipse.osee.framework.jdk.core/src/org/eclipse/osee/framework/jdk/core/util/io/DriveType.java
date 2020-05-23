/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.jdk.core.util.io;

/**
 * @author Roberto E. Escobar
 */
public enum DriveType {
   Unknown((short) 0),
   NoRootDirectory((short) 1),
   Removable((short) 2), // Floppy, Zip, etc
   Fixed((short) 3), // Hard disk
   Remote((short) 4), // Network drive
   CdRom((short) 5),
   RamDrive((short) 6);

   private final short value;

   DriveType(short value) {
      this.value = value;
   }

   public short getValue() {
      return value;
   }
}
