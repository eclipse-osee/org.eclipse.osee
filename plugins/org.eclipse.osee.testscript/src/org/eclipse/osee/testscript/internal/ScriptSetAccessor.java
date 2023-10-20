/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.testscript.internal;

import org.eclipse.osee.accessor.internal.ArtifactAccessorImpl;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Stephen J. Molaro
 */
public class ScriptSetAccessor extends ArtifactAccessorImpl<ScriptSetToken> {

   public ScriptSetAccessor(OrcsApi orcsApi) {
      super(CoreArtifactTypes.ScriptSet, orcsApi);
   }

}