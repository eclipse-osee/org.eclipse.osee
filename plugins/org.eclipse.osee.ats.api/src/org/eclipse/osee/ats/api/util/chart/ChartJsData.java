/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.api.util.chart;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class ChartJsData {
   List<String> labels = new LinkedList<>();
   List<ChartJsDataset> datasets = new LinkedList<>();

   public List<String> getLabels() {
      return labels;
   }

   public void setLabels(List<String> labels) {
      this.labels = labels;
   }

   public List<ChartJsDataset> getDatasets() {
      return datasets;
   }

   public void setDatasets(List<ChartJsDataset> datasets) {
      this.datasets = datasets;
   }

}
