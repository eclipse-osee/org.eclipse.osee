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
package org.eclipse.osee.framework.skynet.core.event;

import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.plugin.event.Event;

/**
 * @author Donald G. Dunne
 */
public abstract class GuidEvent extends Event {

   private Branch branch;
   private String eventId;
   protected Artifact artifact = null;

   public GuidEvent(Object sender) {
      this(sender, null);
   }

   public GuidEvent(Object sender, Exception exception) {
      super(sender, exception);
   }

   public Artifact getArtifact() {
      if (artifact == null) {
         try {
            artifact = ArtifactPersistenceManager.getInstance().getArtifact(getGuid(), branch);
         } catch (SQLException ex) {
            ex.printStackTrace();
         }
      }
      return artifact;
   }

   public String getGuid() {
      return eventId;
   }

   public void setGuid(String eventId, Branch branch) {
      this.eventId = eventId;
      this.branch = branch;
   }

}
