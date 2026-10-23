/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.framework.core.widget;

/**
 * @author Donald G. Dunne
 */
public class SwtWidgetListener {

   int swtOperation;
   Object listener;

   public SwtWidgetListener(int swtOperation, Object listener) {
      this.swtOperation = swtOperation;
      this.listener = listener;
   }

   public int getSwtOperation() {
      return swtOperation;
   }

   public void setSwtOperation(int swtOperation) {
      this.swtOperation = swtOperation;
   }

   public Object getListener() {
      return listener;
   }

   public void setListener(Object listener) {
      this.listener = listener;
   }

}
