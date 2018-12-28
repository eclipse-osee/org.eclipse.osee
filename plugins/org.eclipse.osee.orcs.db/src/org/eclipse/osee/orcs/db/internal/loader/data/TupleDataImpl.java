/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.loader.data;

import org.eclipse.osee.framework.core.data.TupleTypeId;
import org.eclipse.osee.framework.core.data.TupleTypeToken;
import org.eclipse.osee.orcs.core.ds.OrcsVersionedObjectImpl;
import org.eclipse.osee.orcs.core.ds.TupleData;
import org.eclipse.osee.orcs.core.ds.VersionData;
import org.eclipse.osee.orcs.core.internal.tuple.TupleVisitor;

/**
 * @author Angel Avila
 */
public class TupleDataImpl extends OrcsVersionedObjectImpl<TupleTypeToken> implements TupleData {

   private TupleTypeId tupleType;
   private Long element1;
   private Long element2;
   private Long element3;
   private Long element4;

   public TupleDataImpl(VersionData version) {
      super(version);
   }

   @Override
   public void accept(TupleVisitor visitor) {
      visitor.visit(this);
   }

   @Override
   public TupleTypeId getTupleType() {
      return tupleType;
   }

   @Override
   public Long getElement1() {
      return element1;
   }

   @Override
   public Long getElement2() {
      return element2;
   }

   @Override
   public Long getElement3() {
      return element3;
   }

   @Override
   public Long getElement4() {
      return element4;
   }

   @Override
   public void setElement1(Long element1) {
      this.element1 = element1;
   }

   @Override
   public void setElement2(Long element2) {
      this.element2 = element2;
   }

   @Override
   public void setElement3(Long element3) {
      this.element3 = element3;
   }

   @Override
   public void setElement4(Long element4) {
      this.element4 = element4;
   }

   @Override
   public void setTupleType(TupleTypeId tupleType) {
      this.tupleType = tupleType;
   }

   @Override
   public boolean isExistingVersionUsed() {
      return false;
   }

   @Override
   public void setUseBackingData(boolean useBackingData) {
      //
   }

   @Override
   public void setRationale(String rationale) {
      //
   }
}
