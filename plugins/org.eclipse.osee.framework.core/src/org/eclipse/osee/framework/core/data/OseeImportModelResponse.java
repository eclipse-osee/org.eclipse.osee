/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Roberto E. Escobar
 */
public class OseeImportModelResponse {

   private final List<TableData> reportData;
   private boolean wasPersisted;
   private String comparisonSnapshotModel;
   private String comparisonSnapshotModelName;

   public OseeImportModelResponse() {
      this.wasPersisted = false;
      this.reportData = new ArrayList<TableData>();
   }

   public List<TableData> getReportData() {
      return reportData;
   }

   public void setReportData(Collection<TableData> data) {
      reportData.addAll(data);
   }

   public boolean wasPersisted() {
      return wasPersisted;
   }

   public void setPersisted(boolean wasPersisted) {
      this.wasPersisted = wasPersisted;
   }

   public String getComparisonSnapshotModel() {
      return comparisonSnapshotModel;
   }

   public String getComparisonSnapshotModelName() {
      return comparisonSnapshotModelName;
   }

   public void setComparisonSnapshotModel(String comparisonSnapshotModel) {
      this.comparisonSnapshotModel = comparisonSnapshotModel;
   }

   public void setComparisonSnapshotModelName(String comparisonSnapshotModelName) {
      this.comparisonSnapshotModelName = comparisonSnapshotModelName;
   }
}
