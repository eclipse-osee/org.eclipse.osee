/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet;

import java.util.Collection;
import org.eclipse.nebula.widgets.xviewer.XViewerFactory;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;

/**
 * Provides OSEE XViewerFactories to ability to define their osee.target property
 *
 * @author Donald G. Dunne
 */
public abstract class OseeTargetXViewerFactory extends XViewerFactory {

   public OseeTargetXViewerFactory(String namespace) {
      super(namespace);
   }

   public abstract Collection<IUserGroupArtifactToken> getUserGroups();
}
