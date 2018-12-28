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

package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Donald G. Dunne
 */
public class ArtifactChecks {
   private static String ELEMENT_ID = "ArtifactCheck";
   private static String EXTENSION_ID = Activator.PLUGIN_ID + "." + ELEMENT_ID;
   private static String CLASS_NAME_ATTRIBUTE = "classname";
   private static List<IArtifactCheck> checks = null;

   private static final ExtensionDefinedObjects<IArtifactCheck> artifactCheckObjects =
      new ExtensionDefinedObjects<>(EXTENSION_ID, ELEMENT_ID, CLASS_NAME_ATTRIBUTE, true);

   public static List<IArtifactCheck> getArtifactChecks() {
      if (checks == null) {
         checks = new ArrayList<>();
         checks.addAll(artifactCheckObjects.getObjects());
         checks.add(new UserArtifactCheck());
      }
      return checks;
   }
}