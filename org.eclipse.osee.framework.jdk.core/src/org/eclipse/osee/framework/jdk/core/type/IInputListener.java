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
package org.eclipse.osee.framework.jdk.core.type;

import java.util.Collection;

/**
 * @author Roberto E. Escobar
 */
public interface IInputListener<T> {

   void addNode(T node);

   void addNodes(Collection<T> nodes);

   void removeNode(T node);

   void removeAll();

   void nodeChanged(T inNode);

   void inputChanged();
}
