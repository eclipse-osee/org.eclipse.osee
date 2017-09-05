/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
