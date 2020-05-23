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

package org.eclipse.osee.orcs.script.dsl.validation;

import com.google.inject.Inject;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.script.dsl.IExpressionResolver;
import org.eclipse.osee.orcs.script.dsl.IFieldResolver;
import org.eclipse.osee.orcs.script.dsl.IFieldResolver.OsField;
import org.eclipse.osee.orcs.script.dsl.OrcsScriptDslConstants;
import org.eclipse.osee.orcs.script.dsl.OrcsScriptUtil;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectExpression;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectFieldExpression;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectObjectExpression;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsExpression;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampOpClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampRangeClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsVariable;
import org.eclipse.xtext.validation.Check;

/**
 * Custom validation rules. see http://www.eclipse.org/Xtext/documentation.html#validation
 * 
 * @author Roberto E. Escobar
 */
public class OrcsScriptDslJavaValidator extends org.eclipse.osee.orcs.script.dsl.validation.AbstractOrcsScriptDslJavaValidator {

   @Inject
   private IExpressionResolver resolver;

   @Inject
   private IFieldResolver fieldResolver;

   @Check
   public void checkOsVariable(OsVariable variable) {
      String variableName = variable.getName();
      if (!Character.isLowerCase(variableName.charAt(0))) {
         warning(OrcsScriptDslConstants.VALIDATION_ERROR__VARIABLE_NAME__MSG,
            OrcsScriptDslPackage.Literals.OS_VARIABLE__NAME,
            OrcsScriptDslConstants.VALIDATION_ERROR__VARIABLE_NAME__CODE, variableName);
      }
   }

   /**
    * Ensure time range is valid
    */
   @Check
   public void checkOsTxTimestampRangeValidity(OsTxTimestampRangeClause clause) {
      Date fromDate = checkAndGetDate(clause.getFrom(), OrcsScriptDslPackage.OS_TX_TIMESTAMP_RANGE_CLAUSE__FROM);
      Date toDate = checkAndGetDate(clause.getTo(), OrcsScriptDslPackage.OS_TX_TIMESTAMP_RANGE_CLAUSE__TO);
      if (fromDate == null || toDate == null) {
         // nothing to report here
      } else if (fromDate.after(toDate)) {
         String from = OrcsScriptUtil.asDateString(fromDate);
         String to = OrcsScriptUtil.asDateString(toDate);
         String msg = String.format(OrcsScriptDslConstants.VALIDATION_ERROR__TIMESTAMP_RANGE_TEMPLATE__MSG, from, to);
         error(msg, null, OrcsScriptDslPackage.OS_TX_TIMESTAMP_RANGE_CLAUSE,
            OrcsScriptDslConstants.VALIDATION_ERROR__TIMESTAMP_RANGE_INVALID__CODE);
      }
   }

   private Date checkAndGetDate(OsExpression expression, int featureId) {
      Date toReturn = null;
      try {
         toReturn = resolver.resolveSingle(Date.class, expression);
      } catch (Exception ex) {
         error(ex.getMessage(), null, featureId, OrcsScriptDslConstants.CONVERSION_ERROR__BAD_TIMESTAMP_FORMAT__CODE);
      }
      return toReturn;
   }

   /**
    * Ensure time is valid
    */
   @Check
   public void checkOsTxTimestampRangeValidity(OsTxTimestampOpClause clause) {
      OsExpression expression = clause.getTimestamp();
      checkAndGetDate(expression, OrcsScriptDslPackage.OS_TX_TIMESTAMP_OP_CLAUSE__TIMESTAMP);
   }

   /**
    * Ensure only allowed fields are used
    */
   @Check
   public void checkOsCollectObjectExpression(OsCollectObjectExpression object) {
      Set<? extends OsField> unallowed = fieldResolver.getNotAllowedDeclaredFields(object);
      if (!unallowed.isEmpty()) {
         Set<? extends OsField> allowedFields = fieldResolver.getAllowedFields(object);
         String msg =
            String.format(OrcsScriptDslConstants.VALIDATION_ERROR__INVALID_FIELD__MSG, unallowed, allowedFields);
         error(msg, null, OrcsScriptDslPackage.OS_COLLECT_OBJECT_EXPRESSION__EXPRESSIONS,
            OrcsScriptDslConstants.VALIDATION_ERROR__INVALID_FIELD__CODE);
      }
   }

   /**
    * Ensure aliases don't collide
    */
   @Check
   public void checkOsCollectClause(OsCollectClause clause) {
      Set<String> aliases = new HashSet<>();
      Set<String> collisions = new HashSet<>();

      OsCollectExpression expression = clause.getExpression();
      collectAliasHelper(expression, aliases, collisions);

      if (!collisions.isEmpty()) {
         String msg = String.format(OrcsScriptDslConstants.VALIDATION_ERROR__AMBIGUOUS_ALIAS__MSG, collisions);
         error(msg, null, OrcsScriptDslPackage.OS_COLLECT_CLAUSE,
            OrcsScriptDslConstants.VALIDATION_ERROR__AMBIGUOUS_ALIAS__CODE);
      }
   }

   private void collectAliasHelper(OsCollectExpression expression, Set<String> aliases, Set<String> collisions) {
      OsExpression aliasExpression = null;
      List<OsCollectExpression> expressions = null;
      if (expression instanceof OsCollectFieldExpression) {
         OsCollectFieldExpression fieldExp = (OsCollectFieldExpression) expression;
         aliasExpression = fieldExp.getAlias();
      } else if (expression instanceof OsCollectObjectExpression) {
         OsCollectObjectExpression objExpr = (OsCollectObjectExpression) expression;
         aliasExpression = objExpr.getAlias();
         expressions = objExpr.getExpressions();
      }
      if (aliasExpression != null) {
         String alias = resolver.resolveSingle(String.class, aliasExpression);
         if (!Strings.isValid(alias)) {
            alias = expression.getName();
         }
         boolean added = aliases.add(alias);
         if (!added) {
            collisions.add(alias);
         }
      }
      if (expressions != null) {
         for (OsCollectExpression child : expressions) {
            collectAliasHelper(child, aliases, collisions);
         }
      }
   }

}
