/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.framework.core.threading;

import java.util.concurrent.Callable;

/**
 * @author John R. Misinco
 */
public interface ThreadedWorkerFactory<T> {

   public int getWorkSize();

   public Callable<T> createWorker(int startIndex, int endIndex);

}