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
package org.eclipse.osee.framework.skynet.core.commit;

import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public abstract class OseeChange {

   private final TxChange txChange;
   private final int gammaId;
   private final ModificationType modificationType;
   private final int typeId;

   protected OseeChange(TxChange txChange, int gammaId, ModificationType modificationType, int typeId) {
      super();
      this.txChange = txChange;
      this.gammaId = gammaId;
      this.modificationType = modificationType;
      this.typeId = typeId;
   }

   public TxChange getTxChange() {
      return txChange;
   }

   public int getGammaId() {
      return gammaId;
   }

   public ModificationType getModificationType() {
      return modificationType;
   }

   public int getTypeId() {
      return typeId;
   }

   public abstract void accept(IChangeResolver resolver) throws OseeCoreException;
}
