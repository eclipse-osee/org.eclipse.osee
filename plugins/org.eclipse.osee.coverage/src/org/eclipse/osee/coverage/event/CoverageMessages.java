/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.event;

import org.eclipse.osee.framework.messaging.MessageID;

public enum CoverageMessages implements MessageID {
   CoveragePackageSave(true, "Aylfa1rRxx6NQf4MfNwA", "topic:org.eclipse.osee.coverage.msgs.CoveragePackageSave", org.eclipse.osee.coverage.msgs.CoveragePackageSave.class, false);

   private String name;
   private Class<?> clazz;
   boolean isReplyRequired;
   private String guid;
   private boolean isTopic;

   CoverageMessages(boolean isTopic, String guid, String name, Class<?> clazz, boolean isReplyRequired) {
      this.guid = guid;
      this.name = name;
      this.clazz = clazz;
      this.isReplyRequired = isReplyRequired;
      this.isTopic = isTopic;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public Class<?> getSerializationClass() {
      return clazz;
   }

   @Override
   public boolean isReplyRequired() {
      return isReplyRequired;
   }

   @Override
   public String getId() {
      return guid;
   }

   @Override
   public boolean isTopic() {
      return isTopic;
   }
}
