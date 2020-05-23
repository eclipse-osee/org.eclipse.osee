/*********************************************************************
 * Copyright (c) 2018 Boeing
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
      "/*********************************************************************\n * Copyright (c) 2020 Boeing\n *\n * This program and the accompanying materials are made\n * available under the terms of the Eclipse Public License 2.0\n * which is available at https://www.eclipse.org/legal/epl-2.0/\n *\n * SPDX-License-Identifier: EPL-2.0\n *\n * Contributors:\n *     Boeing - initial API and implementation\n **********************************************************************/\n";

   public static void main(String[] args) throws IOException {
      Rule rule = new ReplaceAll(classDeclarationP, distributionStatement);
      rule.setFileNamePattern(args[1]);
      rule.process(new File(args[0]));
   }
}