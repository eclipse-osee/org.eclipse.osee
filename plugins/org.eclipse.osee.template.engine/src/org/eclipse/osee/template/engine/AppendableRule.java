/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.template.engine;

import java.io.IOException;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.type.NamedBase;

/**
 * @author Ryan D. Brooks
 */
public abstract class AppendableRule<T> extends NamedBase {

   public AppendableRule(String ruleName) {
      super(ruleName);
   }

   public AppendableRule() {
      this("unnamed rule");
   }

   public abstract void applyTo(Appendable appendable) throws IOException;

   public void applyTo(Appendable appendable, T data) throws IOException {
      applyTo(appendable);
   }

   public void applyTo(Appendable appendable, Map<String, String> attributes) throws IOException {
      applyTo(appendable);
   }
}