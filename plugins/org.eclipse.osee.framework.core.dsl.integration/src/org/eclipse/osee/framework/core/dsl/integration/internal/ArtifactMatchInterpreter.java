/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.dsl.integration.internal;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.dsl.integration.ArtifactDataProvider.ArtifactProxy;
import org.eclipse.osee.framework.core.dsl.oseeDsl.CompareOp;
import org.eclipse.osee.framework.core.dsl.oseeDsl.CompoundCondition;
import org.eclipse.osee.framework.core.dsl.oseeDsl.Condition;
import org.eclipse.osee.framework.core.dsl.oseeDsl.MatchField;
import org.eclipse.osee.framework.core.dsl.oseeDsl.SimpleCondition;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactMatcher;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XLogicOperator;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactMatchInterpreter {

   public boolean matches(XArtifactMatcher matcher, Collection<ArtifactProxy> proxies) {
      boolean matched = false;
      Iterator<ArtifactProxy> iterator = proxies.iterator();
      while (iterator.hasNext() && !matched) {
         matched = matches(matcher, iterator.next());
         if (matched) {
            break;
         }
      }
      return matched;
   }

   public boolean matches(XArtifactMatcher matcher, ArtifactProxy proxy) {
      List<Condition> conditions = matcher.getConditions();
      List<XLogicOperator> operators = matcher.getOperators();
      return evaluate(conditions, operators, proxy);
   }

   boolean evaluate(List<? extends Condition> conditions, List<? extends XLogicOperator> operators, ArtifactProxy proxy) {
      boolean result = false;
      Iterator<? extends Condition> iteratorConds = conditions.iterator();
      Iterator<? extends XLogicOperator> iteratorOps = operators.iterator();
      if (iteratorConds.hasNext()) {
         Condition lastCondition = iteratorConds.next();
         if (iteratorOps.hasNext()) {
            while (iteratorOps.hasNext() && iteratorConds.hasNext()) {
               XLogicOperator op = iteratorOps.next();
               Condition condition = iteratorConds.next();
               result = evaluate(op, lastCondition, condition, proxy);
            }
         } else {
            result = evaluate(lastCondition, proxy);
         }
      }
      return result;
   }

   boolean evaluate(XLogicOperator op, Condition conditionA, Condition conditionB, ArtifactProxy proxy) {
      boolean result = evaluate(conditionA, proxy);
      if (op == XLogicOperator.AND) {
         return result && evaluate(conditionB, proxy);
      } else if (op == XLogicOperator.OR) {
         return result || evaluate(conditionB, proxy);
      }
      throw new OseeArgumentException("Invalid op defined: %s", op);
   }

   boolean evaluate(Condition condition, ArtifactProxy proxy) {
      if (condition instanceof SimpleCondition) {
         return evaluate((SimpleCondition) condition, proxy);
      } else if (condition instanceof CompoundCondition) {
         CompoundCondition group = (CompoundCondition) condition;
         return evaluate(group.getConditions(), group.getOperators(), proxy);
      }
      throw new OseeArgumentException("Invalid Condition defined: %s", condition);
   }

   boolean evaluate(SimpleCondition condition, ArtifactProxy proxy) {
      String expression = Strings.unquote(condition.getExpression());
      Conditions.checkNotNullOrEmpty(expression, "expression");

      String input = null;
      MatchField field = condition.getField();
      switch (field) {
         case ARTIFACT_ID:
            input = proxy.getGuid();
            Conditions.checkExpressionFailOnTrue(!GUID.isValid(input), "guid");
            break;
         case BRANCH_UUID:
            BranchId branch = proxy.getBranch();
            Conditions.checkExpressionFailOnTrue(branch.isInvalid(),
               String.format("branch id should be > 0, but is [%s]", branch.getIdString()));
            input = branch.getIdString();
            break;
         case ARTIFACT_NAME:
            input = proxy.getName();
            break;
         case BRANCH_NAME:
            input = proxy.getBranchToken().getName();
            break;
         default:
            throw new OseeArgumentException("Invalid field [%s]", field);
      }
      Conditions.checkNotNullOrEmpty(input, field.getName());
      CompareOp op = condition.getOp();
      boolean result = false;
      if (op == CompareOp.EQ) {
         result = input.equals(expression);
      } else if (op == CompareOp.LIKE) {
         Matcher matcher = expressions.get(expression);
         if (matcher == null) {
            Pattern pattern = Pattern.compile(expression);
            matcher = pattern.matcher("");
            expressions.put(expression, matcher);
         }
         synchronized (matcher) {
            matcher.reset(input);
            result = matcher.find();
         }
      } else {
         throw new OseeArgumentException("Invalid CompareOp [%s]", op);
      }
      return result;
   }
   private final Map<String, Matcher> expressions = new ConcurrentHashMap<>();

}
