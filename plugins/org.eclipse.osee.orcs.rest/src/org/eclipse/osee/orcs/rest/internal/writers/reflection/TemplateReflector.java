/*********************************************************************
 * Copyright (c) 2020 Boeing
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
package org.eclipse.osee.orcs.rest.internal.writers.reflection;

import static org.eclipse.jdt.core.dom.ASTNode.BOOLEAN_LITERAL;
import static org.eclipse.jdt.core.dom.ASTNode.METHOD_INVOCATION;
import static org.eclipse.jdt.core.dom.ASTNode.NUMBER_LITERAL;
import static org.eclipse.jdt.core.dom.ASTNode.QUALIFIED_NAME;
import static org.eclipse.jdt.core.dom.ASTNode.STRING_LITERAL;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.orcs.rest.model.GenericReport;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author David W. Miller
 */
public class TemplateReflector {
   Stack<GenericMethodInvoker<GenericReport>> realMethods = new Stack<>();
   private final GenericReport report;
   private final XResultData results;

   public TemplateReflector(GenericReport report, XResultData results) {
      this.report = report;
      this.results = results;
   }

   public void pushMethod(GenericMethodInvoker<GenericReport> method) {
      realMethods.push(method);
   }

   public GenericReport invokeStack(GenericReport report) {
      while (!realMethods.empty()) {
         report = (GenericReport) realMethods.pop().invoke(report);
      }
      return report;
   }

   public Object getArgumentFromASTExpression(Expression expression, ListIterator<MethodInvocation> iterator) {
      int type = expression.getNodeType();
      Object toReturn = null;
      switch (type) {
         case BOOLEAN_LITERAL: {
            toReturn = ((BooleanLiteral) expression).booleanValue();
            break;
         }
         case NUMBER_LITERAL: {
            toReturn = ((NumberLiteral) expression).resolveConstantExpressionValue();
            break;
         }
         case STRING_LITERAL: {
            toReturn = ((StringLiteral) expression).getLiteralValue();
            break;
         }
         case QUALIFIED_NAME: {
            QualifiedName qname = (QualifiedName) expression;
            toReturn = getArgumentFromQualifiedName(qname);
            break;
         }
         case METHOD_INVOCATION: {
            // error if it is not a query invocation
            MethodInvocation queryMethod = (MethodInvocation) expression;
            // the iterator contains the query
            String methodName = queryMethod.getName().getFullyQualifiedName();
            if (queryMethod.toString().startsWith("report.query")) {
               // first figure out how to handle one
               MethodInvocation subQuery = iterator.next();
               String subMethodName = subQuery.getName().getFullyQualifiedName();
               results.logf("QueryMethodName: %s", subMethodName);
               GenericMethodInvoker<QueryBuilder> invoker = new GenericMethodInvoker<>(report.query());
               List<Expression> args = subQuery.arguments();
               List<Object> arguments = new ArrayList<>();
               for (Expression arg : args) {
                  Object result = getArgumentFromASTExpression(arg, iterator);
                  results.logf("    Arg: %s: type %d", arg.toString(), arg.getNodeType());
                  arguments.add(result);
               }
               if (invoker.set(subMethodName, arguments)) {
                  toReturn = invoker.invoke(report.query());
               } else {
                  results.logf("failed to set method for %s", methodName);
               }
            }
            break;
         }
         default: {
            throw new OseeArgumentException("Invalid argument type %d in %s", type, "TemplateReflector");
         }
      }
      return toReturn;
   }

   private Object getArgumentFromQualifiedName(QualifiedName qname) {
      Object o = null;
      String qualifier = qname.getQualifier().toString();
      String name = qname.getName().toString();
      try {
         Class<?> clazz = getClassFromWhiteList(qualifier);
         Field field = clazz.getDeclaredField(name);
         return field.get(null);
      } catch (Exception ex) {
         results.errorf("Failed to get argument for %s, full error: %s", qname, ex.toString());
      }
      return o;
   }

   private Class<?> getClassFromWhiteList(String className) {
      Class<?> toReturn = null;

      if (CoreArtifactTypes.class.getName().endsWith(className)) {
         toReturn = CoreArtifactTypes.class;
      } else if (CoreAttributeTypes.class.getName().endsWith(className)) {
         toReturn = CoreAttributeTypes.class;
      } else if (CoreRelationTypes.class.getName().endsWith(className)) {
         toReturn = CoreRelationTypes.class;
      }
      return toReturn;
   }
}
