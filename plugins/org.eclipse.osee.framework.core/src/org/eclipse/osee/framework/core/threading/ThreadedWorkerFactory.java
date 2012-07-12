/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.threading;

import java.util.concurrent.Callable;

/**
 * @author John R. Misinco
 */
public interface ThreadedWorkerFactory<T> {

   public int getWorkSize();

   public Callable<T> createWorker(int startIndex, int endIndex);

}