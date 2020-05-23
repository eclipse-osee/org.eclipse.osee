/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.vcast.model;

import java.util.Collection;
import org.eclipse.osee.vcast.VCastDataStore;

/**
 * @author Roberto E. Escobar
 */
public class VCastProjectTable implements VCastTableData<VCastProject> {

   @Override
   public String getName() {
      return "projects";
   }

   @Override
   public String[] getColumns() {
      return new String[] {"id", "name", "path"};
   }

   @Override
   public Collection<VCastProject> getRows(VCastDataStore dataStore) {
      return dataStore.getAllProjects();
   }

   @Override
   public Object[] toRow(VCastProject data) {
      Integer id = data.getId();
      String name = data.getName();
      String path = data.getPath();
      return new Object[] {id, name, path};
   }
}
