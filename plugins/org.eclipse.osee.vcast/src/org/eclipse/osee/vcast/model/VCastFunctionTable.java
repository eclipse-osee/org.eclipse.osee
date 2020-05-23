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
public class VCastFunctionTable implements VCastTableData<VCastFunction> {

   @Override
   public String getName() {
      return "functions";
   }

   @Override
   public String[] getColumns() {
      return new String[] {
         "id",
         "instrumented_file_id",
         "findex",
         "name",
         "canonical_name",
         "total_lines",
         "complexity",
         "num_pairs_or_paths"};
   }

   @Override
   public Collection<VCastFunction> getRows(VCastDataStore dataStore) {
      return dataStore.getAllFunctions();
   }

   @Override
   public Object[] toRow(VCastFunction data) {
      Integer id = data.getId();
      Integer instrumentedFileId = data.getInstrumentedFileId();
      Integer findex = data.getFindex();
      String name = data.getName();
      String canonicalName = data.getCanonicalName();
      Integer totalLines = data.getTotalLines();
      Integer complexity = data.getComplexity();
      Integer numPairsOrPaths = data.getNumPairsOrPaths();
      return new Object[] {
         id,
         instrumentedFileId,
         findex,
         name,
         canonicalName,
         totalLines,
         complexity,
         numPairsOrPaths};
   }

}
