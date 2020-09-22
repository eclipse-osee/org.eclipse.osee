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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author David W. Miller
 */
public class ASTParserUtil {
   private final List<String> classPaths = new ArrayList<>();

   public CompilationUnit parse(String javaCode) {
      ASTParser parser = ASTParser.newParser(AST.JLS10);
      parser.setSource(javaCode.toCharArray());
      parser.setKind(ASTParser.K_COMPILATION_UNIT);
      parser.setResolveBindings(true);
      parser.setBindingsRecovery(true);

      Map<String, String> options = JavaCore.getOptions();
      parser.setCompilerOptions(options);
      String unitName = getUnitNameFromCode(javaCode);
      parser.setUnitName(unitName);

      parser.setEnvironment(classPaths.toArray(new String[0]), null, null, true);
      CompilationUnit cUnit = (CompilationUnit) parser.createAST(null);
      return cUnit;
   }

   public CompilationUnit parseFile(String fileName) {
      File toParse = new File(fileName);
      if (toParse.isFile()) {
         try {
            String javaCode = Lib.fileToString(toParse);
            return parse(javaCode);
         } catch (IOException ex) {
            throw new OseeCoreException("AST Parser failure", ex);
         }
      }
      throw new OseeCoreException("Invalid file %s provided to AST Parser", fileName);
   }

   public List<String> getParsedProblems(CompilationUnit parsed) {
      List<String> problems = new ArrayList<>();
      IProblem[] iproblems = parsed.getProblems();
      for (IProblem problem : iproblems) {
         problems.add(problem.getMessage());
      }
      return problems;
   }

   public void addClassPath(String path) {
      classPaths.add(path);
   }

   public List<String> getPaths() {
      return classPaths;
   }

   private String getUnitNameFromCode(String code) {
      Matcher matcher = Pattern.compile("\\sclass\\s+(\\w+)").matcher(code);
      if (matcher.find()) {
         return matcher.group(1);
      }
      throw new OseeArgumentException("ASTParser: No class found in the given text");
   }
}
