/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.config;

import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.osee.framework.core.data.ArtifactId;

/**
 * @author Donald G. Dunne
 */
@XmlRootElement
public class JaxAtsConfigObject extends JaxAtsObject {

   private boolean active;

   public boolean isActive() {
      return active;
   }

   public void setActive(boolean active) {
      this.active = active;
   }

   public final String toStringWithId() {
      return String.format("[%d][%s]", getUuid(), getName());
   }

   public ArtifactId getStoreObject() {
      return null;
   }

   public void setStoreObject(ArtifactId artifact) {
      // do nothing
   }

}
