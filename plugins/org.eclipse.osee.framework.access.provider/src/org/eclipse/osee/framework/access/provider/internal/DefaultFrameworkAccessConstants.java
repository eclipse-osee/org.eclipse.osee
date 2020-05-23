/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.framework.access.provider.internal;

import org.eclipse.osee.framework.core.data.IAccessContextId;

/**
 * @author John R. Misinco
 */
public final class DefaultFrameworkAccessConstants {

   private DefaultFrameworkAccessConstants() {
      //do nothing
   }

   public static final IAccessContextId DEFAULT_FRAMEWORK_CONTEXT =
      IAccessContextId.valueOf(7441402941554657282L, "anonymous.context");

   public final static IAccessContextId INVALID_ASSOC_ART_ID =
      IAccessContextId.valueOf(8528534420990278776L, "famework.invalidAssocArtId");

}
