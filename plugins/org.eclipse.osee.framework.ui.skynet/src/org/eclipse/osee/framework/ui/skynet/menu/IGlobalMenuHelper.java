/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.menu;

import java.util.Collection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.menu.GlobalMenu.GlobalMenuItem;

/**
 * Method is provided so different widget types can provide the artifacts for the GobalMenu to operate on.
 * 
 * @author Donald G. Dunne
 */
public interface IGlobalMenuHelper {

   public Collection<Artifact> getArtifacts();

   public Collection<GlobalMenuItem> getValidMenuItems();

}
