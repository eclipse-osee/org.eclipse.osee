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
