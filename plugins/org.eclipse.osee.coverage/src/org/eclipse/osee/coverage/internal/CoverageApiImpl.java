/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.coverage.internal;

import org.eclipse.osee.coverage.CoverageApi;
import org.eclipse.osee.coverage.CoverageImportApi;
import org.eclipse.osee.coverage.CoverageItemApi;
import org.eclipse.osee.coverage.CoverageProgramApi;
import org.eclipse.osee.coverage.PartitionChartDataApi;
import org.eclipse.osee.coverage.PartitionDefApi;
import org.eclipse.osee.coverage.PartitionResultApi;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Stephen J. Molaro
 */
public class CoverageApiImpl implements CoverageApi {

   private OrcsApi orcsApi;
   private CoverageProgramApi coverageProgramApi;
   private PartitionDefApi partitionDefApi;
   private PartitionResultApi partitionResultApi;
   private PartitionChartDataApi partitionChartDataApi;
   private CoverageItemApi coverageItemApi;
   private CoverageImportApi coverageImportApi;

   public void bindOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public void start() {
      this.coverageProgramApi = new CoverageProgramApiImpl(orcsApi);
      this.partitionDefApi = new PartitionDefApiImpl(orcsApi);
      this.partitionResultApi = new PartitionResultApiImpl(orcsApi);
   }

   @Override
   public OrcsApi getOrcsApi() {
      return this.orcsApi;
   }

   @Override
   public CoverageProgramApi getCoverageProgramApi() {
      return this.coverageProgramApi;
   }

   @Override
   public PartitionDefApi getPartitionDefApi() {
      return this.partitionDefApi;
   }

   @Override
   public PartitionResultApi getPartitionResultApi() {
      return this.partitionResultApi;
   }

   @Override
   public PartitionChartDataApi getPartitionChartDataApi() {
      return this.partitionChartDataApi;
   }

   @Override
   public CoverageItemApi getCoverageItemApi() {
      return this.coverageItemApi;
   }

   @Override
   public CoverageImportApi getCoverageImportApi() {
      return this.coverageImportApi;
   }

}