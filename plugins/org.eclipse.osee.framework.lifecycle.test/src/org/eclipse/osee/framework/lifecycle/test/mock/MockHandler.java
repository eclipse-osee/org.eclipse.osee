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

   public MockHandler() {
      this.status = Status.OK_STATUS;
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
      System.out.println("check");
      return status;
   }

   @Override
   public IStatus onPostCondition(IProgressMonitor monitor) {
      System.out.println("on post");
      return status;
   }

   @Override
   public IStatus onPreCondition(IProgressMonitor monitor) {
      System.out.println("on pre");
      return status;
   }
}