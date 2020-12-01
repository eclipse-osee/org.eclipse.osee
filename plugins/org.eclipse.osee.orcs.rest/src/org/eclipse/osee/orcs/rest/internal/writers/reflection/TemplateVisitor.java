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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodInvocation;

/**
 * @author David W. Miller
 */
public class TemplateVisitor extends ASTVisitor {
   List<MethodInvocation> methods = new ArrayList<>();

   public List<MethodInvocation> getInvocations() {
      return methods;
   }

   @Override
   public boolean visit(MethodInvocation node) {
      return methods.add(node);
   }
}
