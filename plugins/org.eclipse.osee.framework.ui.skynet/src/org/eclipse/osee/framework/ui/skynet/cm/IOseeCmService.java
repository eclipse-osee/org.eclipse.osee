/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.cm;

import java.util.Collection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Roberto E. Escobar
 * @author Donald G. Dunne
 */
public interface IOseeCmService {

   boolean isCmAdmin();

   void openArtifact(Artifact artifact, OseeCmEditor oseeCmEditor);

   void openArtifact(String guid, OseeCmEditor oseeCmEditor);

   void openArtifacts(String name, Collection<Artifact> artifacts, OseeCmEditor oseeCmEditor);

   KeyedImage getOpenImage(OseeCmEditor oseeCmEditor);
}
