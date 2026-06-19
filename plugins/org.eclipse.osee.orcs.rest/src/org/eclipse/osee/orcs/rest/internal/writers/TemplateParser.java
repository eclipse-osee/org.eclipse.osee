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

package org.eclipse.osee.orcs.rest.internal.writers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.internal.writers.reflection.ASTParserUtil;
import org.eclipse.osee.orcs.rest.internal.writers.reflection.GenericMethodInvoker;
import org.eclipse.osee.orcs.rest.internal.writers.reflection.TemplateReflector;
import org.eclipse.osee.orcs.rest.internal.writers.reflection.TemplateVisitor;
import org.eclipse.osee.orcs.rest.model.GenericReport;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author David W. Miller
 */
public final class TemplateParser {
   private final QueryFactory queryApi;
   private final ArtifactId reportTemplateArt;
   private final XResultData results;
   private ArtifactReadable cachedTemplateArtifact;

   public TemplateParser(OrcsApi orcsApi, BranchId branch, ArtifactId view, ArtifactId templateArt, XResultData results) {
      this.queryApi = orcsApi.getQueryFactory();
      this.reportTemplateArt = templateArt;
      this.results = results;
   }

   public ArtifactReadable getTemplateArtifact() {
      if (cachedTemplateArtifact == null) {
         cachedTemplateArtifact = queryApi.fromBranch(CoreBranches.COMMON).andId(reportTemplateArt).getArtifact();
      }
      return cachedTemplateArtifact;
   }

   public void parseTemplateData(GenericReport report) {

      String code = getTemplateArtifact().getSoleAttributeAsString(CoreAttributeTypes.JavaCode);
      ASTParserUtil parser = new ASTParserUtil();
      setPathsForParser(parser);
      CompilationUnit parsedCode = parser.parse(code);
      if (!parsedCode.getAST().hasBindingsRecovery()) {
         results.log("Bindings not activated in Template Parser");
      }
      IProblem[] problems = parsedCode.getProblems();
      for (IProblem problem : problems) {
         results.log("AST Parse Compiler: " + problem.getMessage());
      }
      TemplateVisitor visitor = new TemplateVisitor();
      parsedCode.accept(visitor);

      List<MethodInvocation> methods = visitor.getInvocations();
      List<ImportDeclaration> imports = visitor.getImports();
      TemplateReflector reflector = new TemplateReflector(report, results);
      setReflectionClasses(reflector, imports);
      ListIterator<MethodInvocation> iterator = methods.listIterator();
      while (iterator.hasNext()) {
         MethodInvocation method = iterator.next();
         String methodName = method.getName().getFullyQualifiedName();
         results.logf("MethodName: %s", methodName);
         GenericMethodInvoker<GenericReport> invoker = new GenericMethodInvoker<GenericReport>(report);

         List<Expression> args = method.arguments();
         List<Object> arguments = new ArrayList<>();
         for (Expression arg : args) {
            Object result = reflector.getArgumentFromASTExpression(arg, iterator);
            results.logf("    Arg: %s: type %d", arg.toString(), arg.getNodeType());
            if (result == null) {
               results.errorf("invalid argument type %s", arg.toString());
               return;
            }
            arguments.add(result);
         }
         if (methodName.equals("query")) {
            report = reflector.invokeStack(report);
         } else if (methodName.equals("level") || methodName.equals("relationLevel")) {
            invoker.set(methodName, arguments);
            reflector.pushMethod(invoker);
            report = reflector.invokeStack(report);
         } else if (invoker.set(methodName, arguments)) {
            reflector.pushMethod(invoker);
         } else {
            results.logf("Error setting method %s", methodName);
         }
      }
   }

   private void setReflectionClasses(TemplateReflector reflector, List<ImportDeclaration> imports) {
      // get all imports related to artifact, attribute and relation types
      // assumes the class names have ArtifactType, AttributeType, or RelationType somewhere in the name
      reflector.setAllowedReflectionClass(CoreArtifactTypes.class);
      reflector.setAllowedReflectionClass(CoreAttributeTypes.class);
      reflector.setAllowedReflectionClass(CoreRelationTypes.class);
      for (ImportDeclaration imp : imports) {
         String impName = imp.getName().getFullyQualifiedName();
         if (impName.contains("AttributeType") || impName.contains("ArtifactType") || impName.contains(
            "RelationType")) {
            try {
               Class<?> clazz = Class.forName(impName);
               reflector.setAllowedReflectionClass(clazz);
            } catch (ClassNotFoundException ex) {
               results.logf("classloader could not find %s", impName);
            }
         }
      }
   }

   private void setPathsForParser(ASTParserUtil parser) {
      // add known paths
      try {
         String[] classNames = {
            "org.eclipse.osee.orcs.rest.model.GenericReport",
            "org.eclipse.osee.orcs.rest.internal.writers.GenericReportBuilder",
            "org.eclipse.osee.orcs.search.QueryBuilder",
            "org.eclipse.osee.framework.jdk.core.type.ResultSet",
            "org.eclipse.osee.framework.core.data.ArtifactId"};
         List<String> paths = new ArrayList<>();
         for (String className : classNames) {
            String path = getClasspathEntry(className);
            if (path != null) {
               paths.add(path);
            }
         }
         results.log("Adding base paths");
         for (String path : paths) {
            parser.addClassPath(path);
            results.log(path);
         }

         ArtifactReadable templateArt = getTemplateArtifact();
         if (templateArt.getAttributeCount(CoreAttributeTypes.Annotation) > 0) {
            List<String> attrPaths = templateArt.getAttributeValues(CoreAttributeTypes.Annotation);
            results.logf("Adding paths from template artifact %s", templateArt.getIdString());
            for (String path : attrPaths) {
               if (path.startsWith("relative/")) {
                  String[] relativePath = path.split("/");
                  String relClassPath = getClasspathEntry(relativePath[1]);
                  if (relClassPath != null) {
                     parser.addClassPath(relClassPath);
                  }
               } else if (isSourceDirectory(path)) {
                  parser.addSourcePath(path);
               } else {
                  parser.addClassPath(path);
               }
               results.log(path);
            }
         } else {
            results.logf("No Annotation Based Paths set in annotations for template artifact %s",
               templateArt.getIdString());
         }
      } catch (OseeCoreException ex) {
         results.errorf("failed to add path for TemplateParser: %s", ex.toString());
      }
   }

   /**
    * Resolves the classpath entry (jar or directory) for the given fully-qualified class name. Returns null if the
    * class cannot be found or its code source is unavailable (e.g. OSGi bundle classloaders).
    */
   private String getClasspathEntry(String className) {
      try {
         Class<?> clazz = Class.forName(className);
         java.security.CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();
         if (codeSource == null) {
            results.logf("CodeSource unavailable for %s, skipping", className);
            return null;
         }
         return new File(codeSource.getLocation().toURI()).getPath();
      } catch (ClassNotFoundException ex) {
         results.logf("Class not found: %s", className);
         return null;
      } catch (Exception ex) {
         results.logf("Failed to resolve classpath for %s: %s", className, ex.toString());
         return null;
      }
   }

   /**
    * Determines if the given path is a source directory (contains .java files) rather than a classpath entry (jar or
    * compiled class directory). A path is considered a source directory if it is a directory that does not end with
    * .jar and contains a "src" segment or has .java files.
    */
   private boolean isSourceDirectory(String path) {
      if (path.endsWith(".jar")) {
         return false;
      }
      File dir = new File(path);
      return dir.isDirectory() && !new File(dir, "META-INF").exists();
   }
}