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
package org.eclipse.osee.framework.ui.skynet;

/**
 * @author Roberto E. Escobar
 */
public interface DslGrammar {

   String getExtension();

   String getGrammarId();

   <T> T getObject(Class<? extends T> clazz);

   DslGrammarStorageAdapter getStorageAdapter();
}
