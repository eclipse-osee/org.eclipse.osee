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

/**
 * @author Ryan D. Brooks
 */
public final class StringRule extends AppendableRule {
   private final CharSequence value;

   public StringRule(String ruleName, CharSequence value) {
      super(ruleName);
      this.value = value;
   }

   @Override
   public void applyTo(Appendable appendable) throws IOException {
      appendable.append(value);
   }

   @Override
   public String toString() {
      return value.toString();
   }
}