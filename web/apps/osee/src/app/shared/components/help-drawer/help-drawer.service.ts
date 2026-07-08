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
import { HelpTopicRegistryService } from './help-topic-registry.service';

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
	private readonly registry = inject(HelpTopicRegistryService);

	/** The currently highlighted anchor ID (for directive-based highlighting). */
	readonly highlightedAnchor = signal<string>('');

	/** Reference to the popup window, if open. */
	private helpWindow: Window | null = null;

	/** Interval ID for tracking popup window close. */
	private closeCheckInterval: ReturnType<typeof setInterval> | null = null;

	/** Whether the help popup is currently open. */
	readonly isOpen = signal(false);

	/** The currently active topic ID. */
	readonly activeTopic = signal<string>('');

	private readonly origin = typeof window !== 'undefined' ? window.location.origin : '';

	constructor() {
		// Listen for messages from the popup window
		window.addEventListener('message', (event) => {
			if (event.origin !== this.origin) {
				return;
			}
			if (event.data?.type === 'osee-help-highlight') {
				if (event.data.anchorId) {
					this.highlightAnchor(event.data.anchorId);
				}
			} else if (event.data?.type === 'osee-help-request-sections') {
				const topic = this.registry.getTopic(event.data.topicId);
				if (topic && this.helpWindow && !this.helpWindow.closed) {
					this.helpWindow.postMessage(
						{
							type: 'osee-help-sections-response',
							sections: topic.sections ?? [],
						},
						this.origin
					);
				}
			}
		});
	}

	/** Opens help in a popup window for the specified topic. */
	open(topicId: string): void {
		this.activeTopic.set(topicId);
		const url = `${this.document.location.origin}${this.getBasePath()}/help-popup?topic=${topicId}`;

		if (this.helpWindow && !this.helpWindow.closed) {
			this.helpWindow.location.href = url;
			this.helpWindow.focus();
		} else {
			this.helpWindow = window.open(
				url,
				'osee-help',
				'width=480,height=700,scrollbars=yes,resizable=yes'
			);
		}
		this.isOpen.set(true);

		// Clear any existing interval before starting a new one
		if (this.closeCheckInterval) {
			clearInterval(this.closeCheckInterval);
		}

		// Track window close
		this.closeCheckInterval = setInterval(() => {
			if (!this.helpWindow || this.helpWindow.closed) {
				this.isOpen.set(false);
				this.helpWindow = null;
				if (this.closeCheckInterval) {
					clearInterval(this.closeCheckInterval);
					this.closeCheckInterval = null;
				}
			}
		}, 500);
	}

	/** Closes the help popup window. */
	close(): void {
		if (this.closeCheckInterval) {
			clearInterval(this.closeCheckInterval);
			this.closeCheckInterval = null;
		}
		if (this.helpWindow && !this.helpWindow.closed) {
			this.helpWindow.close();
		}
		this.helpWindow = null;
		this.isOpen.set(false);
		this.clearHighlight();
	}

	/** Toggles the help popup for the specified topic. */
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

	private getBasePath(): string {
		// Detect base href from the document
		const base = this.document.querySelector('base');
		const href = base?.getAttribute('href') ?? '/';
		return href.endsWith('/') ? href.slice(0, -1) : href;
	}
}
