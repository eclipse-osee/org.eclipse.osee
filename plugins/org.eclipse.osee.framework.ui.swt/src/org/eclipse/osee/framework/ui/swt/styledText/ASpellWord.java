/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.swt.styledText;

/**
 * @author Donald G. Dunne
 */
public class ASpellWord {
   public String word;
   public int start;

   public ASpellWord(String word, int start) {
      this.word = word;
      this.start = start;
   }
}
