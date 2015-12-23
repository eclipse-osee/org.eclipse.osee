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
import org.eclipse.osee.framework.core.data.TokenFactory;

/**
 * @author John R. Misinco
 */
public final class DefaultFrameworkAccessConstants {

   private DefaultFrameworkAccessConstants() {
      //do nothing
   }

   public static final IAccessContextId DEFAULT_FRAMEWORK_CONTEXT =
      TokenFactory.createAccessContextId("AeA3J6AvwgEG4HGsypAA", "guest.context");

   public final static IAccessContextId INVALID_ASSOC_ART_ID =
      TokenFactory.createAccessContextId("ASwQLzdXySQfeyG57ywA", "famework.invalidAssocArtId");

   public static final String STORAGE_ARTIFACT_NAME = "Framework Access Model";

}
