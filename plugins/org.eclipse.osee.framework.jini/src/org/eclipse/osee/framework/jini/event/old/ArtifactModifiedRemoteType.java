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
package org.eclipse.osee.framework.jini.event.old;

/**
 * This class defines the popup event type. Any listener which desires to receive these events can establish a
 * subscription for PopupEventType.class.getCanonicalName().
 * 
 * @author Donald G. Dunne
 */
public class ArtifactModifiedRemoteType extends OseeRemoteEventInstance {

   private static final long serialVersionUID = 1665518299340674326L;
   private String guid;
   private String type;

   public ArtifactModifiedRemoteType(String eventGuid, String changedGuid, String type) {
      super(eventGuid);
      this.guid = changedGuid;
      this.type = type;
   }

   public String getChangedGuid() {
      return guid;
   }

   public String getType() {
      return type;
   }

}
