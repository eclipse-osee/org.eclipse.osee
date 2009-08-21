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

import org.eclipse.osee.framework.core.exception.OseeArgumentException;

/**
 * @author Robert A. Fisher
 */
public class RelationType implements Comparable<RelationType> {
   private final int relationTypeId;
   private final String namespace;
   private final String typeName;
   private final String sideAName;
   private final String sideBName;
   private final String aToBPhrasing;
   private final String bToAPhrasing;
   private final String shortName;
   private final String ordered;
   private final String defaultOrderTypeGuid;

   public RelationType(int linkTypeId, String namespace, String typeName, String sideAName, String sideBName, String aToBPhrasing, String bToAPhrasing, String shortName, String ordered, String defaultOrderTypeGuid) {
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
      this.defaultOrderTypeGuid = defaultOrderTypeGuid;
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

   public boolean isSideAName(String sideName) throws OseeArgumentException {
      if (!sideAName.equals(sideName) && !sideBName.equals(sideName)) {
         throw new OseeArgumentException("sideName does not match either of the available side names");
      }

      return sideAName.equals(sideName);
   }

   public int compareTo(RelationType descriptor) {
      return typeName.compareTo(descriptor.getTypeName());
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof RelationType) {
         return relationTypeId == ((RelationType) obj).relationTypeId;
      }
      return false;
   }

   @Override
   public int hashCode() {
      return 17 * relationTypeId;
   }

   @Override
   public String toString() {
      return typeName + " " + sideAName + " <--> " + sideBName;
   }

   public int getRelationTypeId() {
      return relationTypeId;
   }

   public boolean isOrdered() {
      return ordered.equalsIgnoreCase("Yes");
   }
   
   public String getDefaultOrderTypeGuid(){
      return defaultOrderTypeGuid;
   }
}