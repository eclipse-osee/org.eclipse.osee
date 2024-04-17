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

package org.eclipse.osee.coverage;

import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Stephen J. Molaro
 */
public interface CoverageApi {
   OrcsApi getOrcsApi();

   CoverageProgramApi getCoverageProgramApi();

   PartitionDefApi getPartitionDefApi();

   PartitionResultApi getPartitionResultApi();

   PartitionChartDataApi getPartitionChartDataApi();

   CoverageItemApi getCoverageItemApi();

   CoverageImportApi getCoverageImportApi();

}