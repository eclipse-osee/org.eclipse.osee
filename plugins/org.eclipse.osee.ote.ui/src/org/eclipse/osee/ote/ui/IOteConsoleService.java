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
package org.eclipse.osee.ote.ui;

import java.io.IOException;
import org.eclipse.osee.framework.jdk.core.util.IConsoleInputListener;

/**
 * @author Roberto E. Escobar
 */
public interface IOteConsoleService {

   void addInputListener(IConsoleInputListener listener);

   void removeInputListener(IConsoleInputListener listener);

   void write(String value);

   void write(String value, int type, boolean popup);

   void writeError(String string);

   void prompt(String str) throws IOException;

   void popup();
}
