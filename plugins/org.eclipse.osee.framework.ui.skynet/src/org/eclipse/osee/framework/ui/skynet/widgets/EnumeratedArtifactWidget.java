/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.framework.ui.skynet.widgets;

import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * Used by XWidgets that perform data binding using OSEE attributes
 *
 * @author Donald G. Dunne
 */
public interface EnumeratedArtifactWidget {

   public ArtifactToken getEnumeratedArt();

   public void setEnumeratedArt(ArtifactToken enumeratedArt);

}