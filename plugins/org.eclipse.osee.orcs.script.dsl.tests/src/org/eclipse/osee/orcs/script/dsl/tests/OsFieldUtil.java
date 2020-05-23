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

import java.util.Arrays;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslFactory;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactQueryStatement;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchQueryStatement;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectExpression;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectFieldExpression;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectObjectExpression;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFollowRelationType;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsStringLiteral;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxQueryStatement;

/**
 * @author Roberto E. Escobar
 */
public final class OsFieldUtil {

   private static final OrcsScriptDslFactory factory = OrcsScriptDslFactory.eINSTANCE;

   public static enum Scope {
      BRANCH_QUERY,
      TX_QUERY,
      ARTIFACT_QUERY,
      FOLLOW_RELATION;
   }

   public static OsCollectClause newCollectClause(Scope scope, String objectName, String... fields) {
      return newCollectClauseHelper(scope, objectName, null, false, fields);
   }

   public static OsCollectClause newCollectClauseWithAlias(Scope scope, String objectName, String alias, String... fieldsAndAliases) {
      return newCollectClauseHelper(scope, objectName, alias, true, fieldsAndAliases);
   }

   private static OsCollectClause newCollectClauseHelper(Scope scope, String objectName, String alias, boolean hasAliases, String... fieldsAndAliases) {
      OsCollectClause clause = factory.createOsCollectClause();
      switch (scope) {
         case BRANCH_QUERY:
            OsBranchQueryStatement br = factory.createOsBranchQueryStatement();
            br.setCollect(clause);
            break;
         case TX_QUERY:
            OsTxQueryStatement tx = factory.createOsTxQueryStatement();
            tx.setCollect(clause);
            break;
         case ARTIFACT_QUERY:
            OsArtifactQueryStatement art = factory.createOsArtifactQueryStatement();
            art.setCollect(clause);
            break;
         case FOLLOW_RELATION:
            OsFollowRelationType rel = factory.createOsFollowRelationType();
            rel.setCollect(clause);
            break;
         default:
            break;
      }
      OsCollectObjectExpression expression = newCollectObject(objectName, alias, hasAliases, fieldsAndAliases);
      clause.setExpression(expression);
      return clause;
   }

   public static OsCollectObjectExpression newCollectObject(String objectName, String... fields) {
      return newCollectObject(objectName, null, false, fields);
   }

   private static OsCollectObjectExpression newCollectObject(String objectName, String alias, boolean hasAliases, String... fields) {
      OsCollectObjectExpression expression = factory.createOsCollectObjectExpression();
      expression.setName(objectName);
      if (alias != null) {
         OsStringLiteral stringLiteral = factory.createOsStringLiteral();
         stringLiteral.setValue(alias);
         expression.setAlias(stringLiteral);
      }
      if (Arrays.asList(fields).contains("*")) {
         OsCollectExpression field = factory.createOsCollectAllFieldsExpression();
         expression.getExpressions().add(field);
      } else {
         if (hasAliases) {
            // every other is an alias
            OsCollectFieldExpression fieldExpr = null;
            OsStringLiteral stringLiteral = null;
            for (String value : fields) {
               if (fieldExpr == null) {
                  fieldExpr = factory.createOsCollectFieldExpression();
                  fieldExpr.setName(value);
                  expression.getExpressions().add(fieldExpr);
               } else {
                  stringLiteral = factory.createOsStringLiteral();
                  stringLiteral.setValue(value);
                  fieldExpr.setAlias(stringLiteral);
                  fieldExpr = null;
               }
            }
         } else {
            for (String field : fields) {
               OsCollectExpression fieldExpr = factory.createOsCollectFieldExpression();
               fieldExpr.setName(field);
               expression.getExpressions().add(fieldExpr);
            }
         }
      }
      return expression;
   }

}
