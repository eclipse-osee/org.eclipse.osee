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
package org.eclipse.osee.coverage.internal.vcast.operations;

import java.io.File;
import java.net.URI;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.vcast.CoverageImportData;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Roberto E. Escobar
 */
public class VCastOperationFactory {

   public static IOperation createValidateAggregateReportOp(XResultData logger, String vcastDirectory, CoverageImport coverageImport) throws OseeCoreException {
      File reportHtmlFile = new File(vcastDirectory + "/vcast/vcast_aggregate_coverage_report.html");
      Conditions.checkExpressionFailOnTrue(!reportHtmlFile.exists(), "VectorCast file doesn't exist [%s]",
         reportHtmlFile.getAbsoluteFile());

      URI reportUri = reportHtmlFile.toURI();
      VCastValidateAggregateReportOperation op =
         new VCastValidateAggregateReportOperation(logger, coverageImport, reportUri);
      return op;
   }

   public static IOperation createVerifyVcpFilesOp(XResultData logger, CoverageImportData input, VCastVcp vCastVcp) {
      return new VCastVerifyVcpOperation(logger, input, vCastVcp);
   }

}
