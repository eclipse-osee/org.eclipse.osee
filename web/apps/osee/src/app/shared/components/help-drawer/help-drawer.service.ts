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
import { inject, Injectable, signal } from '@angular/core';
import { DOCUMENT } from '@angular/common';

export type HelpTopic = {
	readonly id: string;
	readonly label: string;
	readonly markdownPath: string;
	readonly sections?: HelpSection[];
};

export type HelpSection = {
	readonly id: string;
	readonly label: string;
	readonly anchorId: string;
};

@Injectable({
	providedIn: 'root',
})
export class HelpDrawerService {
	private readonly document = inject(DOCUMENT);

	private readonly helpOpenClass = 'osee-help-open';

	/** Whether the drawer is currently open. */
	readonly isOpen = signal(false);

	/** The currently active topic ID. */
	readonly activeTopic = signal<string>('');

	/** The currently highlighted anchor ID (for UI annotation). */
	readonly highlightedAnchor = signal<string>('');

	/** Opens the help drawer to the specified topic. */
	open(topicId: string): void {
		this.activeTopic.set(topicId);
		this.isOpen.set(true);
		this.document.body.classList.add(this.helpOpenClass);
	}

	/** Closes the help drawer. */
	close(): void {
		this.isOpen.set(false);
		this.clearHighlight();
		this.document.body.classList.remove(this.helpOpenClass);
	}

	/** Toggles the help drawer for the specified topic. */
	toggle(topicId: string): void {
		if (this.isOpen() && this.activeTopic() === topicId) {
			this.close();
		} else {
			this.open(topicId);
		}
	}

	/** Highlights a UI element by its anchor ID. */
	highlightAnchor(anchorId: string): void {
		this.highlightedAnchor.set(anchorId);
	}

	/** Clears the current highlight. */
	clearHighlight(): void {
		this.highlightedAnchor.set('');
	}
}
