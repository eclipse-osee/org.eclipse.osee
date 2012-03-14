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
package org.eclipse.osee.framework.skynet.core.change;

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

public class ChangeWorkerUtil {

   public static IChangeWorker create(Change change, Artifact artifact) {
      IChangeWorker changeWorker = null;

      switch (change.getChangeType()) {
         case artifact:
            changeWorker = new ArtifactChangeWorker(change, artifact);
            break;
         case attribute:
            changeWorker = new AttributeChangeWorker(change, artifact);
            break;
         case relation:
            changeWorker = new RelationChangeWorker(change, artifact);
            break;
      }
      return changeWorker;
   }
}
