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

import java.util.Collection;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.ChangeType;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ModificationType;
import org.eclipse.swt.graphics.Image;

/**
 * @author Robert A. Fisher
 */
public class AttributeSummary extends ChangeSummary<AttributeChange> implements IAttributeChange {
   private static final long serialVersionUID = 4461778311833295250L;

   /**
    * Constructor for deserialization.
    */
   protected AttributeSummary() {

   }

   /**
    * @param changes
    */
   public AttributeSummary(Collection<AttributeChange> changes) {
      super(changes);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.revision.IAttributeChange#getChange()
    */
   public String getChange() {
      return getNewestChange().getChange();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.revision.IAttributeChange#getWasValue()
    */
   public String getWasValue() {
      return getOldestChange().getWasValue();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.revision.IAttributeChange#getName()
    */
   public String getName() {
      return getNewestChange().getName();
   }

   protected Image getImage(ChangeType changeType, ModificationType modType) {
      return AttributeChange.getImage(changeType, modType);
   }
}
