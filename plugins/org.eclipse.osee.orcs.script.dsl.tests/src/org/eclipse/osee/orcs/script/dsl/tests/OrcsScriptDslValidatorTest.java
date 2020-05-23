/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.orcs.script.dsl.tests;

import static org.eclipse.osee.orcs.script.dsl.OrcsScriptDslConstants.VALIDATION_ERROR__TIMESTAMP_RANGE_INVALID__CODE;
import static org.eclipse.osee.orcs.script.dsl.OrcsScriptDslConstants.VALIDATION_ERROR__TIMESTAMP_RANGE_TEMPLATE__MSG;
import static org.eclipse.osee.orcs.script.dsl.tests.OsFieldUtil.newCollectClauseWithAlias;
import com.google.inject.Inject;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.osee.orcs.script.dsl.OrcsScriptDslConstants;
import org.eclipse.osee.orcs.script.dsl.OrcsScriptDslInjectorProvider;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslFactory;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsStringLiteral;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampOpClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampRangeClause;
import org.eclipse.osee.orcs.script.dsl.tests.OsFieldUtil.Scope;
import org.eclipse.osee.orcs.script.dsl.validation.OrcsScriptDslJavaValidator;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.junit4.validation.AssertableDiagnostics;
import org.eclipse.xtext.junit4.validation.ValidatorTester;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test Case for {@link OrcsScriptDslValidator}
 * 
 * @author Roberto E. Escobar
 */
@InjectWith(OrcsScriptDslInjectorProvider.class)
@RunWith(XtextRunner.class)
public class OrcsScriptDslValidatorTest {

   @Inject
   private ValidatorTester<OrcsScriptDslJavaValidator> validator;

   private final OrcsScriptDslFactory factory = OrcsScriptDslFactory.eINSTANCE;

   @Test
   public void testOsTxTimeStampRangeValidity() {
      String fromDate = "09/10/2014 11:00:12 AM";
      String toDate = "09/10/2014 11:00:12 AM";

      OsTxTimestampClause clause = newTimestampRange(fromDate, toDate);

      AssertableDiagnostics diagnostics = validator.validate(clause);
      diagnostics.assertOK();
   }

   @Test
   public void testOsTxTimeStampRangeValidityRangeError() {
      String fromDate = "09/10/2014 12:00:12 PM";
      String toDate = "09/10/2014 11:00:12 AM";

      OsTxTimestampClause clause = newTimestampRange(fromDate, toDate);

      AssertableDiagnostics diagnostics = validator.validate(clause);
      diagnostics.assertDiagnosticsCount(1);

      String errorMsg = String.format(VALIDATION_ERROR__TIMESTAMP_RANGE_TEMPLATE__MSG, fromDate, toDate);
      diagnostics.assertError(VALIDATION_ERROR__TIMESTAMP_RANGE_INVALID__CODE, errorMsg);
   }

   @Test
   public void testOsTxTimeStampConversionError1() {
      String date = "09/10/2014 12:00:12";

      OsTxTimestampClause clause = newTimestampOp(date);

      AssertableDiagnostics diagnostics = validator.validate(clause);
      diagnostics.assertDiagnosticsCount(1);

      String errorMsg = String.format("Invalid timestamp format - format should be [%s] or [",
         OrcsScriptDslConstants.TIMESTAMP_FORMAT);
      diagnostics.assertError(OrcsScriptDslConstants.CONVERSION_ERROR__BAD_TIMESTAMP_FORMAT__CODE, errorMsg);
   }

   @Test
   public void testOsTxTimeStampConversionError2() {
      String fromDate = "09/10/2014 12:00:12";
      String toDate = "09/10/2014 11:00:12 AM";

      OsTxTimestampClause clause = newTimestampRange(fromDate, toDate);

      AssertableDiagnostics diagnostics = validator.validate(clause);
      diagnostics.assertDiagnosticsCount(1);

      String errorMsg = String.format("Invalid timestamp format - format should be [%s] or [",
         OrcsScriptDslConstants.TIMESTAMP_FORMAT);
      diagnostics.assertError(OrcsScriptDslConstants.CONVERSION_ERROR__BAD_TIMESTAMP_FORMAT__CODE, errorMsg);
   }

   @Test
   public void testOsCollectStatement1() {
      EObject data = newCollectClauseWithAlias(Scope.ARTIFACT_QUERY, "artifacts", "alias-1", //
         "id", "alias-2", "guid", "alias-3", "mod-type", "alias-4", "attributes", "alias-5", "relations", "alias-6",
         "txs", "alias-7");

      AssertableDiagnostics diagnostics = validator.validate(data);
      diagnostics.assertOK();
   }

   @Test
   public void testOsCollectStatement2() {
      EObject data = newCollectClauseWithAlias(Scope.ARTIFACT_QUERY, "artifacts", //
         "alias-1", //
         "id", null, "guid", "alias-3", "mod-type", null, "attributes", null, "relations", "alias-6", "txs", null);

      AssertableDiagnostics diagnostics = validator.validate(data);
      diagnostics.assertOK();
   }

   @Test
   public void testOsCollectStatementFieldError1() {
      EObject data = newCollectClauseWithAlias(Scope.ARTIFACT_QUERY, "artifacts", //
         "alias-1", //
         "uri", null, "txs", "alias-2");
      AssertableDiagnostics diagnostics = validator.validate(data);
      String errorMsg = String.format(OrcsScriptDslConstants.VALIDATION_ERROR__INVALID_FIELD__MSG, "[uri]",
         "[gamma-id, guid, id, mod-type, type, txs, attributes, relations]");
      diagnostics.assertError(OrcsScriptDslConstants.VALIDATION_ERROR__INVALID_FIELD__CODE, errorMsg);
   }

   @Test
   public void testOsCollectStatementAliasError1() {
      EObject data = newCollectClauseWithAlias(Scope.ARTIFACT_QUERY, "artifacts", //
         "alias-1", //
         "id", "alias-3", "attributes", "alias-3", "relations", "alias-6", "txs", null);

      AssertableDiagnostics diagnostics = validator.validate(data);
      String errorMsg = String.format(OrcsScriptDslConstants.VALIDATION_ERROR__AMBIGUOUS_ALIAS__MSG, "[alias-3]");
      diagnostics.assertError(OrcsScriptDslConstants.VALIDATION_ERROR__AMBIGUOUS_ALIAS__CODE, errorMsg);
   }

   @Test
   public void testOsCollectStatementAliasError2() {
      EObject data = newCollectClauseWithAlias(Scope.ARTIFACT_QUERY, "artifacts", //
         "alias-1", //
         "id", null, "id", null, "relations", "alias-3", "txs", "alias-4");
      AssertableDiagnostics diagnostics = validator.validate(data);
      String errorMsg = String.format(OrcsScriptDslConstants.VALIDATION_ERROR__AMBIGUOUS_ALIAS__MSG, "[id]");
      diagnostics.assertError(OrcsScriptDslConstants.VALIDATION_ERROR__AMBIGUOUS_ALIAS__CODE, errorMsg);
   }

   private OsTxTimestampClause newTimestampOp(String date) {
      OsTxTimestampOpClause toReturn = factory.createOsTxTimestampOpClause();
      OsStringLiteral ts = factory.createOsStringLiteral();
      toReturn.setTimestamp(ts);
      ts.setValue(date);
      return toReturn;
   }

   private OsTxTimestampClause newTimestampRange(String fromDate, String toDate) {
      OsTxTimestampRangeClause toReturn = factory.createOsTxTimestampRangeClause();
      OsStringLiteral fromTs = factory.createOsStringLiteral();
      OsStringLiteral toTs = factory.createOsStringLiteral();
      toReturn.setFrom(fromTs);
      toReturn.setTo(toTs);
      fromTs.setValue(fromDate);
      toTs.setValue(toDate);
      return toReturn;
   }
}
