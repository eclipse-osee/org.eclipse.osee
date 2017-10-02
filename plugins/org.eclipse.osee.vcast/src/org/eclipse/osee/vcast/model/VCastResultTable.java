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
import org.eclipse.osee.vcast.VCastDataStore;

/**
 * @author Roberto E. Escobar
 */
public class VCastResultTable implements VCastTableData<VCastResult> {

   @Override
   public String getName() {
      return "results";
   }

   @Override
   public String[] getColumns() {
      return new String[] {"id", "name", "project_id", "path", "fullname", "enabled", "imported"};
   }

   @Override
   public Collection<VCastResult> getRows(VCastDataStore dataStore)  {
      return dataStore.getAllResults();
   }

   @Override
   public Object[] toRow(VCastResult data) {
      Integer id = data.getId();
      String name = data.getName();
      Integer projectId = data.getProjectId();
      String path = data.getPath();
      String fullname = data.getFullname();
      boolean enabled = data.isEnabled();
      boolean imported = data.isImported();
      return new Object[] {id, name, projectId, path, fullname, enabled, imported};
   }
}
