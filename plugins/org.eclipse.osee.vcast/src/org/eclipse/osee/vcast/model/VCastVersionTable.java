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
public class VCastVersionTable implements VCastTableData<VCastVersion> {

   @Override
   public String getName() {
      return "version";
   }

   @Override
   public String[] getColumns() {
      return new String[] {"version", "date_created"};
   }

   @Override
   public Collection<VCastVersion> getRows(VCastDataStore dataStore)  {
      return Collections.singleton(dataStore.getVersion());
   }

   @Override
   public Object[] toRow(VCastVersion data) {
      return new Object[] {data.getVersion(), data.getDateCreated()};
   }
}
