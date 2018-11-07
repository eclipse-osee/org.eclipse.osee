/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
