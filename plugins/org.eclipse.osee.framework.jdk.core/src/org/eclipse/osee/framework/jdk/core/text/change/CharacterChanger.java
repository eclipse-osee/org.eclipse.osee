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

package org.eclipse.osee.framework.jdk.core.text.change;

import java.io.IOException;
import java.io.Writer;

/**
 * @author Ryan D. Brooks
 */
public interface CharacterChanger {
   public int getStartIndex();

   public int getEndIndex();

   public int applyChange(char[] dest, int destPos);

   public void applyChange(Writer writer) throws IOException;

   public CharacterChanger next();

   public void setNext(CharacterChanger next);

   public int getLengthDelta();
}
