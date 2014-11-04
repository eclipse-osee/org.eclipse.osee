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
public class VCastVersion {

   private final int version;
   private final String dateCreated;

   public VCastVersion(int version, String dateCreated) {
      super();
      this.version = version;
      this.dateCreated = dateCreated;
   }

   public int getVersion() {
      return version;
   }

   public String getDateCreated() {
      return dateCreated;
   }

}
