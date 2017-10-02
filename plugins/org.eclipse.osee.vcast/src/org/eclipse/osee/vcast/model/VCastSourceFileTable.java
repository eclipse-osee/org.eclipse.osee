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
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.vcast.VCastDataStore;

/**
 * @author Roberto E. Escobar
 */
public class VCastSourceFileTable implements VCastTableData<VCastSourceFile> {

   @Override
   public String getName() {
      return "source_files";
   }

   @Override
   public String[] getColumns() {
      return new String[] {"id", "path", "display_name", "checksum", "display_path"};
   }

   @Override
   public Collection<VCastSourceFile> getRows(VCastDataStore dataStore)  {
      return dataStore.getAllSourceFiles();
   }

   @Override
   public Object[] toRow(VCastSourceFile data) {
      Integer id = data.getId();
      String path = data.getPath();
      String displayName = data.getDisplayName();
      Integer checksum = data.getChecksum();
      String displayPath = data.getDisplayPath();
      return new Object[] {id, path, displayName, checksum, displayPath};
   }
}
