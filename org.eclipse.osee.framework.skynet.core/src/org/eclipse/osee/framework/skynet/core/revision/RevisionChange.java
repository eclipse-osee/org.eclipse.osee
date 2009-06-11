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

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.skynet.core.change.ChangeType;

/**
 * @author Jeff C. Phillips
 */
public abstract class RevisionChange implements IRevisionChange, IAdaptable {
   private ChangeType changeType;
   private ModificationType modType;
   private long gammaId;

   /**
    * Constructor for deserialization.
    */
   protected RevisionChange() {

   }

   public RevisionChange(ChangeType changeType, ModificationType modType, long gammaId) {
      super();
      this.changeType = changeType;
      this.modType = modType;
      this.gammaId = gammaId;
   }

   public abstract String getChange();

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.revision.IRevisionChange#getGammaId()
    */
   public long getGammaId() {
      return gammaId;
   }

   public ModificationType getModificationType() {
      return modType;
   }

   /**
    * @return Returns the changeType.
    */
   public ChangeType getChangeType() {
      return changeType;
   }
}
