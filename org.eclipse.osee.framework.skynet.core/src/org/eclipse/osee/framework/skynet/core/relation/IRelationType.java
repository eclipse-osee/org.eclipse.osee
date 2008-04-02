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


public interface IRelationType extends Comparable<IRelationType> {

   /**
    * @return Returns the aToBPhrasing.
    */
   public String getAToBPhrasing();

   /**
    * @return Returns the bToAPhrasing.
    */
   public String getBToAPhrasing();

   /**
    * @return Returns the name.
    */
   public String getTypeName();

   /**
    * @return the namespace
    */
   public String getNamespace();

   public String getSideName(boolean sideA);

   /**
    * @return Returns the sideAName.
    */
   public String getSideAName();

   /**
    * @return Returns the sideBName.
    */
   public String getSideBName();

   /**
    * @return Returns the shortName;
    */
   public String getShortName();

   public boolean isSideAName(String sideName);

   public int getRelationTypeId();

   public void setLinkSideRestriction(int artTypeId, LinkSideRestriction linkSideRestriction);

   public boolean canLinkType(int id);

   public int getRestrictionSizeFor(int id, boolean sideA);
}
