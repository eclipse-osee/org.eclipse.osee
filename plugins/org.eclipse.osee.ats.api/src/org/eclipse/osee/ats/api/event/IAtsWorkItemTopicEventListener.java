/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.event;

import java.util.Collection;
import org.eclipse.osee.ats.api.util.AtsTopicEvent;
import org.eclipse.osee.framework.core.data.ArtifactId;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWorkItemTopicEventListener {

   void handleEvent(AtsTopicEvent topicEvent, Collection<ArtifactId> workItems);

}
