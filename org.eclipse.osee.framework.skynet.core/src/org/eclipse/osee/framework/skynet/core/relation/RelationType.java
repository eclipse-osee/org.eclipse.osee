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
package org.eclipse.osee.framework.skynet.core.relation;

/**
 * @author Robert A. Fisher
 */
public class RelationType implements Comparable<RelationType> {
   private int relationTypeId;
   private String namespace;
   private String typeName;
   private String sideAName;
   private String sideBName;
   private String aToBPhrasing;
   private String bToAPhrasing;
   private String shortName;
   private String ordered;

   public RelationType(int linkTypeId, String namespace, String typeName, String sideAName, String sideBName, String aToBPhrasing, String bToAPhrasing, String shortName, String ordered) {
      super();
      this.relationTypeId = linkTypeId;
      this.namespace = namespace == null ? "" : namespace;
      this.typeName = typeName;
      this.sideAName = sideAName;
      this.sideBName = sideBName;
      this.aToBPhrasing = aToBPhrasing;
      this.bToAPhrasing = bToAPhrasing;
      this.shortName = shortName;
      this.ordered = ordered;
   }

   /**
    * @return Returns the aToBPhrasing.
    */
   public String getAToBPhrasing() {
      return aToBPhrasing;
   }

   /**
    * @return Returns the bToAPhrasing.
    */
   public String getBToAPhrasing() {
      return bToAPhrasing;
   }

   public String getNamespace() {
      return namespace;
   }

   /**
    * @return Returns the name.
    */
   public String getTypeName() {
      return typeName;
   }

   public String getSideName(RelationSide relationSide) {
      return relationSide == RelationSide.SIDE_A ? sideAName : sideBName;
   }

   /**
    * @return Returns the sideAName.
    */
   public String getSideAName() {
      return sideAName;
   }

   /**
    * @return Returns the sideBName.
    */
   public String getSideBName() {
      return sideBName;
   }

   /**
    * @return Returns the shortName.
    */
   public String getShortName() {
      return shortName;
   }

   public boolean isSideAName(String sideName) {

      if (!sideAName.equals(sideName) && !sideBName.equals(sideName)) throw new IllegalArgumentException(
            "sideName does not match either of the available side names");

      return sideAName.equals(sideName);
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Comparable#compareTo(T)
    */
   public int compareTo(RelationType descriptor) {
      return typeName.compareTo(descriptor.getTypeName());
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      if (obj instanceof RelationType) return typeName.equals(((RelationType) obj).typeName);
      return false;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      return typeName.hashCode();
   }

   public String toString() {
      return typeName + " " + sideAName + " <--> " + sideBName;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.relation.RelationType#getRelationTypeId()
    */
   public int getRelationTypeId() {
      return relationTypeId;
   }

   public boolean isOrdered() {
      return ordered.equalsIgnoreCase("Yes");
   }
}
