/*********************************************************************
 * Copyright (c) 2024 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.core.data;

/**
 * @author Luciano T. Vaglienti
 */
public class FallbackAttribute<T> implements IAttribute<T> {
   private final Long id;
   private final T value;
   private final AttributeTypeToken attributeType;
   private final GammaId gammaId;
   private final TransactionDetails latestTxDetails = new TransactionDetails(TransactionId.SENTINEL,
      BranchToken.SENTINEL, null, null, -1, ArtifactId.SENTINEL, -1L, ArtifactId.SENTINEL);

   public FallbackAttribute(long id, AttributeTypeToken attributeType, T value) {
      this.id = id;
      this.attributeType = attributeType;
      this.value = value;
      this.gammaId = GammaId.SENTINEL;
   }

   public FallbackAttribute(long id, AttributeTypeToken attributeType, GammaId gamma, T value) {
      this.id = id;
      this.attributeType = attributeType;
      this.value = value;
      this.gammaId = gamma;
   }

   @Override
   public Long getId() {
      return this.id;
   }

   @Override
   public T getValue() {
      return this.value;
   }

   @Override
   public GammaId getGammaId() {
      return this.gammaId;
   }

   @Override
   public AttributeTypeToken getAttributeType() {
      return this.attributeType;
   }

   @Override
   public String getDisplayableString() {
      return "";
   }

   @Override
   public TransactionDetails getLatestTxDetails() {
      return latestTxDetails;
   }

}
