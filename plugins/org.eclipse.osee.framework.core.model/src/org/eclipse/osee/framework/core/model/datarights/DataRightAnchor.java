/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.framework.core.model.datarights;

import java.util.Objects;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * @author Angel Avila
 */
public class DataRightAnchor implements ToMessage {

   private ArtifactId id;
   private DataRight dataRight;
   private boolean isSetDataRightFooter = false;
   private boolean isContinuous = false;

   public ArtifactId getId() {
      return id;
   }

   public void setId(ArtifactId id) {
      this.id = id;
   }

   public DataRight getDataRight() {
      return dataRight;
   }

   public void setDataRight(DataRight dataRight) {
      this.dataRight = dataRight;
   }

   public boolean isSetDataRightFooter() {
      return isSetDataRightFooter;
   }

   public void setSetDataRightFooter(boolean isSetDataRightFooter) {
      this.isSetDataRightFooter = isSetDataRightFooter;
   }

   public boolean isContinuous() {
      return isContinuous;
   }

   public void setContinuous(boolean isContinuous) {
      this.isContinuous = isContinuous;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Message toMessage(int indent, Message message) {
      var outMessage = Objects.nonNull(message) ? message : new Message();

      var dataRightString = this.dataRight.getContent();
      var dataRightLength = dataRightString.length() < 20 ? dataRightString.length() : 20;

      //@formatter:off
      outMessage
         .indent( indent )
         .title( "Data Right Anchor" )
         .indentInc()
         .segment( "artifactId",   this.id   )
         .segment( "newFooter",    this.isSetDataRightFooter )
         .segment( "isContinuous", this.isContinuous )
         .segment( "dataRight",    dataRightString.subSequence( 0, dataRightLength ) )
         .indentDec()
         ;
      //@formatter:on

      return outMessage;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String toString() {
      return this.toMessage(0, null).toString();
   }
}
