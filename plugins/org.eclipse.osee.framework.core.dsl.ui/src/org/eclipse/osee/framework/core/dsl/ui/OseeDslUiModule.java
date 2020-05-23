/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.framework.core.dsl.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Use this class to register components to be used within the IDE.
 * 
 * @author Roberto E. Escobar
 */
public class OseeDslUiModule extends org.eclipse.osee.framework.core.dsl.ui.AbstractOseeDslUiModule {
   public OseeDslUiModule(AbstractUIPlugin plugin) {
      super(plugin);
   }

   //	public Class<? extends IResourceForEditorInputFactory> bindIResourceForEditorInputFactory() {
   //		return ResourceForResourceWorkingCopyEditorInputFactory.class;
   //	}
}
