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
package org.eclipse.osee.framework.skynet.core.artifact;

/**
 * @author Robert A. Fisher
 */
public class ArtifactToLoadDescription {
   private int artId;
   private int artTypeId;
   private int gammaId;
   private String guid;
   private String humandReadableId;
   private String factoryKey;

   /**
    * @param artId
    * @param guid
    * @param factoryKey
    * @param branch
    */
   public ArtifactToLoadDescription(int artId, int artTypeId, String guid, String humandReadableId, String factoryKey, int gammaId) {
      this.artId = artId;
      this.guid = guid;
      this.gammaId = gammaId;
      this.humandReadableId = humandReadableId;
      this.factoryKey = factoryKey;
      this.artTypeId = artTypeId;
   }

   /**
    * @return Returns the artId.
    */
   public int getArtId() {
      return artId;
   }

   /**
    * @return Returns the factoryKey.
    */
   public String getFactoryKey() {
      return factoryKey;
   }

   /**
    * @return Returns the guid.
    */
   public String getGuid() {
      return guid;
   }

   /**
    * @return Returns the humandReadableId.
    */
   public String getHumandReadableId() {
      return humandReadableId;
   }

   /**
    * @return Returns the artTypeId.
    */
   public int getArtTypeId() {
      return artTypeId;
   }

   /**
    * @return the gammaId
    */
   public int getGammaId() {
      return gammaId;
   }
}
