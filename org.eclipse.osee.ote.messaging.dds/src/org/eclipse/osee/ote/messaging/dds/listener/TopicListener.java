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
package org.eclipse.osee.ote.messaging.dds.listener;

import org.eclipse.osee.ote.messaging.dds.entity.Topic;
import org.eclipse.osee.ote.messaging.dds.status.InconsistentTopicStatus;

/**
 * This class is here for future functionality that is described in the DDS specification
 * but has not been implemented or used.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public interface TopicListener extends Listener{
   public void onInconsistentTopic(Topic theTopic, InconsistentTopicStatus status);
}
