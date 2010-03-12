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
package org.eclipse.osee.framework.ui.data.model.editor.model;

/**
 * @author Roberto E. Escobar
 */
public class RelationDataType extends DataType {

   private String sideAName;
   private String sideBName;
   private String aToBPhrase;
   private String bToAPhrase;
   private String shortName;
   private boolean ordered;

   public RelationDataType() {
      this(EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, false, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING);
   }

   public RelationDataType(String name, String aToBPhrase, String bToAPhrase, boolean ordered, String shortName, String sideAName, String sideBName) {
      this(EMPTY_STRING, name, aToBPhrase, bToAPhrase, ordered, shortName, sideAName, sideBName);
   }

   public RelationDataType(String typeId, String name, String aToBPhrase, String bToAPhrase, boolean ordered, String shortName, String sideAName, String sideBName) {
      super(typeId, name);
      this.aToBPhrase = aToBPhrase;
      this.bToAPhrase = bToAPhrase;
      this.ordered = ordered;
      this.shortName = shortName;
      this.sideAName = sideAName;
      this.sideBName = sideBName;
   }

   /**
    * @return the sideAName
    */
   public String getSideAName() {
      return sideAName;
   }

   /**
    * @param sideAName the sideAName to set
    */
   public void setSideAName(String sideAName) {
      this.sideAName = sideAName;
   }

   /**
    * @return the sideBName
    */
   public String getSideBName() {
      return sideBName;
   }

   /**
    * @param sideBName the sideBName to set
    */
   public void setSideBName(String sideBName) {
      this.sideBName = sideBName;
   }

   /**
    * @return the aToBPhrase
    */
   public String getAToBPhrase() {
      return aToBPhrase;
   }

   /**
    * @param toBPhrase the aToBPhrase to set
    */
   public void setAToBPhrase(String toBPhrase) {
      aToBPhrase = toBPhrase;
   }

   /**
    * @return the bToAPhrase
    */
   public String getBToAPhrase() {
      return bToAPhrase;
   }

   /**
    * @param toAPhrase the bToAPhrase to set
    */
   public void setBToAPhrase(String toAPhrase) {
      bToAPhrase = toAPhrase;
   }

   /**
    * @return the shortName
    */
   public String getShortName() {
      return shortName;
   }

   /**
    * @param shortName the shortName to set
    */
   public void setShortName(String shortName) {
      this.shortName = shortName;
   }

   /**
    * @return the ordered
    */
   public boolean getOrdered() {
      return ordered;
   }

   /**
    * @param ordered the ordered to set
    */
   public void setOrdered(boolean ordered) {
      this.ordered = ordered;
   }

}
