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

package org.eclipse.osee.define.rest.internal.reflection;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.define.api.GenericReport;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author David W. Miller
 */
public final class TemplateParser {
   private final QueryFactory queryApi;
   private final IOseeBranch branch;
   private final ArtifactId view;
   private final ArtifactId reportTemplateArt;
   private final ActivityLog activityLog;

   public TemplateParser(ActivityLog activityLog, OrcsApi orcsApi, BranchId branch, ArtifactId view, ArtifactId templateArt) {
      this.activityLog = activityLog;
      this.queryApi = orcsApi.getQueryFactory();
      this.branch = orcsApi.getQueryFactory().branchQuery().andId(branch).getResultsAsId().getExactlyOne();
      this.view = view;
      this.reportTemplateArt = templateArt;
   }

   public ArtifactReadable getTemplateArtifact() {
      return queryApi.fromBranch(branch).andId(reportTemplateArt).getArtifact();
   }

   public void parseTemplateData(GenericReport report) {

      String code = getTemplateArtifact().getSoleAttributeAsString(CoreAttributeTypes.JavaCode);
      CompilationUnit parsedCode = this.parse(code);

      TemplateVisitor visitor = new TemplateVisitor();
      parsedCode.accept(visitor);

      List<MethodInvocation> methods = visitor.getInvocations();
      TemplateReflector reflector = new TemplateReflector(report, activityLog);
      ListIterator<MethodInvocation> iterator = methods.listIterator();
      while (iterator.hasNext()) {
         MethodInvocation method = iterator.next();
         String methodName = method.getName().getFullyQualifiedName();
         activityLog.getDebugLogger().info("MethodName: %s", methodName);
         GenericMethodInvoker<GenericReport> invoker = new GenericMethodInvoker<GenericReport>(report);

         List<Expression> args = method.arguments();
         List<Object> arguments = new ArrayList<>();
         for (Expression arg : args) {
            Object result = reflector.getArgumentFromASTExpression(arg, iterator);
            activityLog.getDebugLogger().info("    Arg: %s: type %d", arg.toString(), arg.getNodeType());
            arguments.add(result);
         }
         if (methodName.equals("query")) {
            report = reflector.invokeStack(report);
         } else if (invoker.set(methodName, arguments)) {
            reflector.pushMethod(invoker);
         } else {
            activityLog.getDebugLogger().info("Error setting method %s", methodName);
         }
      }
   }

   private CompilationUnit parse(String javaCode) {
      ASTParser parser = ASTParser.newParser(AST.JLS10);
      parser.setKind(ASTParser.K_COMPILATION_UNIT);
      parser.setSource(javaCode.toCharArray());
      parser.setResolveBindings(true);
      return (CompilationUnit) parser.createAST(null);
   }
}