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

package org.eclipse.osee.framework.lifecycle.test.mock;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.lifecycle.LifecycleOpHandler;

/**
 * @author Roberto E. Escobar
 * @author Jeff C. Phillips
 */
public class MockHandler implements LifecycleOpHandler {

   private String a;
   private String b;
   private IStatus status;
   boolean hasRan;

   public MockHandler() {
      this.status = Status.OK_STATUS;
      this.hasRan = false;
   }

   public void setData(String a, String b) {
      this.a = a;
      this.b = b;
   }

   public String getA() {
      return a;
   }

   public String getB() {
      return b;
   }

   public void setStatus(IStatus status) {
      this.status = status;
   }

   public IStatus getStatus() {
      return status;
   }

   @Override
   public IStatus onCheck(IProgressMonitor monitor) {
      hasRan = true;
      return status;
   }

   @Override
   public IStatus onPostCondition(IProgressMonitor monitor) {
      hasRan = true;
      return status;
   }

   @Override
   public IStatus onPreCondition(IProgressMonitor monitor) {
      hasRan = true;
      return status;
   }

   public void doSomething() {
      hasRan = true;
   }

   public boolean hasRan() {
      return hasRan;
   }
}