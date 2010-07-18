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
package org.eclipse.osee.framework.messaging;

/**
 * @author Roberto E. Escobar
 */
public enum SystemTopic implements MessageID {

	JMS_HEALTH_STATUS("jms.health.status"),
	KILL_TEST_JMS_BROKER("jms.kill.broker");

	private String name;

	SystemTopic(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Class<?> getSerializationClass() {
		return null;
	}

	@Override
	public boolean isReplyRequired() {
		return false;
	}

	@Override
	public String getId() {
		return name;
	}

   @Override
   public boolean isTopic() {
      return true;
   }
}
