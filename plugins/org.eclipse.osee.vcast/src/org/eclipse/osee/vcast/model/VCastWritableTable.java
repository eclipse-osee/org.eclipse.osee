/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.vcast.model;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.vcast.VCastDataStore;

/**
 * @author Roberto E. Escobar
 */
public class VCastWritableTable implements VCastTableData<VCastWritable> {

   @Override
   public String getName() {
      return "writable";
   }

   @Override
   public String[] getColumns() {
      return new String[] {"is_writable"};
   }

   @Override
   public Collection<VCastWritable> getRows(VCastDataStore dataStore) {
      return Collections.singleton(dataStore.getWritable());
   }

   @Override
   public Object[] toRow(VCastWritable data) {
      return new Object[] {data.getIsWritable()};
   }
}
