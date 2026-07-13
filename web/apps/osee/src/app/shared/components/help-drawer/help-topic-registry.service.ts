/*********************************************************************
 * Copyright (c) 2026 Boeing
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
import { Injectable } from '@angular/core';
import { HelpTopic } from './help-drawer.service';

/**
 * Registry of help topics. Components self-register by injecting
 * this service and calling register() during initialization.
 */
@Injectable({
	providedIn: 'root',
})
export class HelpTopicRegistryService {
	private readonly topicMap = new Map<string, HelpTopic>();

	getTopic(id: string): HelpTopic | undefined {
		return this.topicMap.get(id);
	}

	getAllTopics(): HelpTopic[] {
		return [...this.topicMap.values()];
	}

	register(topic: HelpTopic): void {
		this.topicMap.set(topic.id, topic);
	}
}
