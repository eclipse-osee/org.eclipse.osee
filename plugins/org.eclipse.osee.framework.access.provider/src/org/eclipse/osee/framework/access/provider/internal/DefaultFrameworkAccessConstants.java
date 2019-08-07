/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
