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
public class VCastProjectFileTable implements VCastTableData<VCastProjectFile> {

   @Override
   public String getName() {
      return "project_files";
   }

   @Override
   public String[] getColumns() {
      return new String[] {"project_id", "source_file_id", "instrumented_file_id", "timestamp", "build_md5sum"};
   }

   @Override
   public Collection<VCastProjectFile> getRows(VCastDataStore dataStore) {
      return dataStore.getAllProjectFiles();
   }

   @Override
   public Object[] toRow(VCastProjectFile data) {
      Integer projectId = data.getProjectId();
      Integer sourceFileId = data.getSourceFileId();
      Integer instrumentedFileId = data.getInstrumentedFileId();
      Integer timestamp = data.getTimestamp();
      String buildMd5sum = data.getBuildMd5Sum();
      return new Object[] {projectId, sourceFileId, instrumentedFileId, timestamp, buildMd5sum};
   }
}
