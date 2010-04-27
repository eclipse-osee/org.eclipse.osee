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
package org.eclipse.osee.framework.skynet.core.change;

import org.eclipse.osee.framework.core.data.TransactionDelta;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.core.model.Branch;

/**
 * @author Jeff C. Phillips
 */
public final class AttributeChangeBuilder extends ChangeBuilder {
   private final String isValue;
   private String wasValue;
   private final int attrId;
   private final AttributeType attributeType;
   private final ModificationType artModType;

   public AttributeChangeBuilder(Branch branch, ArtifactType artifactType, int sourceGamma, int artId, TransactionDelta txDelta, ModificationType modType, boolean isHistorical, String isValue, String wasValue, int attrId, AttributeType attributeType, ModificationType artModType) {
      super(branch, artifactType, sourceGamma, artId, txDelta, modType, isHistorical);
      this.isValue = isValue;
      this.wasValue = wasValue;
      this.attrId = attrId;
      this.attributeType = attributeType;
      this.artModType = artModType;
   }

   public ModificationType getArtModType() {
      return artModType;
   }

   public void setWasValue(String wasValue) {
      this.wasValue = wasValue;
   }

   @Override
   public Change build(Branch branch) throws OseeDataStoreException, OseeTypeDoesNotExist, ArtifactDoesNotExist {
      return new AttributeChange(branch, getSourceGamma(), getArtId(), getTxDelta(), getModType(), isValue, wasValue,
            attrId, attributeType, artModType, isHistorical(), loadArtifact(), new ArtifactDelta(getTxDelta(),
                  loadArtifact(), null));
   }
}
