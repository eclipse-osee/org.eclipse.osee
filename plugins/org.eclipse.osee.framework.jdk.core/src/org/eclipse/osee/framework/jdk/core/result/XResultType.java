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
package org.eclipse.osee.framework.jdk.core.result;

import org.eclipse.osee.framework.jdk.core.result.XResultData.Type;

/**
 * @author Donald G. Dunne
 */
public enum XResultType {

   Error(Type.Severe),
   Warning(Type.Warning),
   Info(Type.Info);

   private final Type rType;

   private XResultType(XResultData.Type rType) {
      this.rType = rType;
   }

   public Type getrType() {
      return rType;
   }

}
