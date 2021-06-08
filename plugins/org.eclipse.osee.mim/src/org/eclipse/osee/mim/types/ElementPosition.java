/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.mim.types;

import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;

/**
 * @author Luciano T. Vaglienti
 */
public class ElementPosition {
   //tbd don't know what the structure should look like for returning position of element
   private List<ArtifactId> messages;
   private List<ArtifactId> submessages;
   private List<ArtifactId> structures;

   public ElementPosition() {
   }

   /**
    * @return the messages
    */
   public List<ArtifactId> getMessages() {
      return messages;
   }

   /**
    * @param messages the messages to set
    */
   public void setMessages(List<ArtifactId> messages) {
      this.messages = messages;
   }

   /**
    * @return the submessages
    */
   public List<ArtifactId> getSubmessages() {
      return submessages;
   }

   /**
    * @param submessages the submessages to set
    */
   public void setSubmessages(List<ArtifactId> submessages) {
      this.submessages = submessages;
   }

   /**
    * @return the structures
    */
   public List<ArtifactId> getStructures() {
      return structures;
   }

   /**
    * @param structures the structures to set
    */
   public void setStructures(List<ArtifactId> structures) {
      this.structures = structures;
   }

}
