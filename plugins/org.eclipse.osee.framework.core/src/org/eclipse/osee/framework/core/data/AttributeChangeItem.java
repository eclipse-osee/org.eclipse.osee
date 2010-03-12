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
package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.core.enums.ModificationType;

/**
 * @author Jeff C. Phillips
 */
public class AttributeChangeItem extends ChangeItem {

   public AttributeChangeItem(long currentSourceGammaId, ModificationType currentSourceModType, int currentSourceTransactionNumber, int attrId, int artId, String value) {
      super(currentSourceGammaId, currentSourceModType, currentSourceTransactionNumber);

      this.setItemId(attrId);
      this.setArtId(artId);
      this.getCurrentVersion().setValue(value);
   }

}
