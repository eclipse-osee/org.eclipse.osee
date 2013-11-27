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
package org.eclipse.osee.coverage.vcast;

import org.eclipse.osee.coverage.internal.vcast.operations.VCastAdaCoverage_V5_3_ImportOperation;
import org.eclipse.osee.coverage.internal.vcast.operations.VCastAdaCoverage_V6_0_ImportOperation;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Roberto E. Escobar
 */
public class CoverageImportFactory {

   private CoverageImportFactory() {
      // Static Factory
   }

   public static IOperation createAdaVCast53ImportOp(CoverageImportData params, CoverageImport coverageImport) throws OseeCoreException {
      Conditions.checkNotNull(params, "params");
      Conditions.checkNotNull(coverageImport, "coverageImport");
      return new VCastAdaCoverage_V5_3_ImportOperation(params, coverageImport);
   }

   public static IOperation createAdaVCast60ImportOp(VCast60Params params, CoverageImport coverageImport) throws OseeCoreException {
      Conditions.checkNotNull(params, "params");
      Conditions.checkNotNull(coverageImport, "coverageImport");
      return new VCastAdaCoverage_V6_0_ImportOperation(params, coverageImport);
   }

}
