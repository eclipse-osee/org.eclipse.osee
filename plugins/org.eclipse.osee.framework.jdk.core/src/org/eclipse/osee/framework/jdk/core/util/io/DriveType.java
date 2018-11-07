/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
