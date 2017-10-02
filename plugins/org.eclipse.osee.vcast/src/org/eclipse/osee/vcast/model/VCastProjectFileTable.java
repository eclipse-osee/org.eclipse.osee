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
