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
public class VCastInstrumentedFileTable implements VCastTableData<VCastInstrumentedFile> {

   @Override
   public String getName() {
      return "instrumented_files";
   }

   @Override
   public String[] getColumns() {
      return new String[] {"id", "source_file_id", "project_id", "unit_index", "coverage_type", "LIS_file", "checksum"};
   }

   @Override
   public Collection<VCastInstrumentedFile> getRows(VCastDataStore dataStore) {
      return dataStore.getAllInstrumentedFiles();
   }

   @Override
   public Object[] toRow(VCastInstrumentedFile data) {
      Integer id = data.getId();
      Integer sourceFileId = data.getSourceFileId();
      Integer projectId = data.getProjectId();
      Integer unitIndex = data.getUnitIndex();
      VCastCoverageType coverageType = data.getCoverageType();
      String lisFile = data.getLISFile();
      Integer checksum = data.getChecksum();
      return new Object[] {id, sourceFileId, projectId, unitIndex, coverageType, lisFile, checksum};
   }
}
