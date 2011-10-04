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
package org.eclipse.osee.orcs.core.internal.artifact;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.AttributeContainer;
import org.eclipse.osee.orcs.core.internal.attribute.Attribute;
import org.eclipse.osee.orcs.data.ReadableAttribute;

public class Artifact implements AttributeContainer {
   // Place-holder

   void setTransactionId(int transactionId) {
      //
   }

   public void internalSetPersistenceData(int gammaId, int transactionId, ModificationType modType, boolean historical) {

   }

   @Override
   public void add(IAttributeType type, Attribute<?> attribute) {
   }

   @Override
   public int getCount(IAttributeType type) {
      return 0;
   }

   @Override
   public Collection<IAttributeType> getAttributeTypes() throws OseeCoreException {
      return null;
   }

   @Override
   public <T> List<ReadableAttribute<T>> getAttributes(IAttributeType attributeType) throws OseeCoreException {
      return null;
   }

   @Override
   public boolean isLoaded() {
      return false;
   }

   @Override
   public void setLoaded(boolean value) {
   }

   @Override
   public <T> ReadableAttribute<T> getSoleAttribute(IAttributeType attributeType) throws OseeCoreException {
      return null;
   }
}
