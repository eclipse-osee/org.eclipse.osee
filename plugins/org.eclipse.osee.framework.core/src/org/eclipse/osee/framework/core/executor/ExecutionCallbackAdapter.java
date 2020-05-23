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

package org.eclipse.osee.framework.core.executor;

/**
 * @author Roberto E. Escobar
 */
public class ExecutionCallbackAdapter<T> implements ExecutionCallback<T> {

   @Override
   public void onCancelled() {
      // Sub-class
   }

   @Override
   public void onSuccess(T result) {
      // Sub-class
   }

   @Override
   public void onFailure(Throwable throwable) {
      // Sub-class
   }

}
