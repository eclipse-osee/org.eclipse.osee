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

/**
 * @author Jeff C. Phillips
 */
public class ArtifactChangeItem extends ChangeItem {

   public ArtifactChangeItem(long currentSourceGammaId, ModificationType currentSourceModType, int currentSourceTransactionNumber, int artId) {
      super(currentSourceGammaId, currentSourceModType, currentSourceTransactionNumber);

      this.setItemId(artId);
      this.setArtId(artId);
   }
}
