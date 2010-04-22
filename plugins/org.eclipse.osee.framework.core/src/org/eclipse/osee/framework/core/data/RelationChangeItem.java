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
public class RelationChangeItem extends ChangeItem {
   private final int bArtId;
   private final String rationale;

   public RelationChangeItem(int relLinkId, int relTypeId, long currentSourceGammaId, ModificationType currentSourceModType, int aArtId, int bArtId, String rationale) {
      super(relLinkId, relTypeId, aArtId, currentSourceGammaId, currentSourceModType);

      this.getCurrentVersion().setValue(rationale);
      this.bArtId = bArtId;
      this.rationale = rationale;
   }

   public int getBArtId() {
      return bArtId;
   }

   public String getRationale() {
      return rationale;
   }
}
