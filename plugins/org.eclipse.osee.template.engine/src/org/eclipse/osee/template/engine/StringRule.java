/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.template.engine;

import java.io.IOException;

/**
 * @author Ryan D. Brooks
 */
public final class StringRule extends AppendableRule<CharSequence> {
   private final CharSequence value;

   public StringRule(String ruleName, CharSequence value) {
      super(ruleName);
      this.value = value;
   }

   @Override
   public void applyTo(Appendable appendable) throws IOException {
      applyTo(appendable, value);
   }

   @Override
   public void applyTo(Appendable appendable, CharSequence data) throws IOException {
      appendable.append(data);
   }

   @Override
   public String toString() {
      return value.toString();
   }
}