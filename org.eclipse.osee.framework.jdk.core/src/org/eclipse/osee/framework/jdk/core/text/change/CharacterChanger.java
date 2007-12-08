/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
