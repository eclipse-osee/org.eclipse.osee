/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.jdk.core.text.rules;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.text.Rule;

/**
 * @author Ryan D. Brooks
 */
public class AddDistributionStatement {
   private static final Pattern classDeclarationP = Pattern.compile(
      "^([/\\s\\*]*(?:Created on [^\n]+)?[\\s\\*]*(?:PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE)?[/\\s\\*]*)package");
   private static final String distributionStatement =
      "/*******************************************************************************\n * Copyright (c) 2010 Boeing.\n * All rights reserved. This program and the accompanying materials\n * are made available under the terms of the Eclipse Public License v1.0\n * which accompanies this distribution, and is available at\n * http://www.eclipse.org/legal/epl-v10.html\n *\n * Contributors:\n *     Boeing - initial API and implementation\n *******************************************************************************/\n";

   public static void main(String[] args) throws IOException {
      Rule rule = new ReplaceAll(classDeclarationP, distributionStatement);
      rule.setFileNamePattern(args[1]);
      rule.process(new File(args[0]));
   }
}
