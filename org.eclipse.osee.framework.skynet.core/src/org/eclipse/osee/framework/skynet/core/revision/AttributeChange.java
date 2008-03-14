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
package org.eclipse.osee.framework.skynet.core.revision;

import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ModificationType.DELETE;
import java.io.InputStream;
import org.eclipse.osee.framework.skynet.core.change.ChangeIcons;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.ChangeType;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ModificationType;
import org.eclipse.swt.graphics.Image;

/**
 * @author Robert A. Fisher
 */
public class AttributeChange extends RevisionChange implements IAttributeChange {
   private static final long serialVersionUID = -8403712077455896863L;

   private int attrId;
   private String name;
   private String isValue;
   private String wasValue;

   @SuppressWarnings("unused")
   transient private InputStream isContent;
   @SuppressWarnings("unused")
   transient private InputStream wasContent;

   /**
    * Constructor for serialization.
    */
   protected AttributeChange() {

   }

   /**
    * Constructor for making new and modified attribute changes.
    * 
    * @param modType
    * @param gammaId
    */
   public AttributeChange(ChangeType changeType, ModificationType modType, int attrId, long gammaId, String name, String isValue, InputStream isContent, String wasValue, InputStream wasContent) {
      super(changeType, modType, gammaId);
      this.attrId = attrId;
      this.name = name;
      this.isValue = isValue;
      this.isContent = isContent;
      this.wasValue = wasValue;
      this.wasContent = wasContent;
   }

   /**
    * Constructor for making deleted attribute changes.
    * 
    * @param gammaId
    */
   public AttributeChange(ChangeType changeType, int attrId, long gammaId, String name, String wasValue) {
      super(changeType, DELETE, gammaId);
      this.attrId = attrId;
      this.name = name;
      this.isValue = null;
      this.wasValue = wasValue;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.revision.IAttributeChange#getChange()
    */
   @Override
   public String getChange() {
      if (getModType() == DELETE)
         return "<deleted>";
      else
         return isValue;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.revision.IAttributeChange#getWasValue()
    */
   public String getWasValue() {
      return wasValue;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.revision.IAttributeChange#getName()
    */
   public String getName() {
      return name;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.revision.IAttributeChange#getImage()
    */
   @Override
   public Image getImage() {
      return ChangeIcons.getImage(getChangeType(), getModType());
   }

   /**
    * @return Returns the attrId.
    */
   public int getAttrId() {
      return attrId;
   }

   @SuppressWarnings("unchecked")
   public Object getAdapter(Class adapter) {
      if (adapter == null) throw new IllegalArgumentException("adapter can not be null");

      if (adapter.isInstance(this)) {
         return this;
      }
      return null;
   }
}
