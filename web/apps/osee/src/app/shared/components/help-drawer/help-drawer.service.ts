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

	/** The currently highlighted anchor ID (for UI annotation). */
	readonly highlightedAnchor = signal<string>('');

	/** Reference to the popup window, if open. */
	private helpWindow: Window | null = null;

	/** Whether the help popup is currently open. */
	readonly isOpen = signal(false);

	/** The currently active topic ID. */
	readonly activeTopic = signal<string>('');

	constructor() {
		// Listen for highlight messages from the popup window
		window.addEventListener('message', (event) => {
			if (
				event.data?.type === 'osee-help-highlight' &&
				event.data?.anchorId
			) {
				this.highlightAnchor(event.data.anchorId);
			}
		});
	}

	/** Opens help in a popup window for the specified topic. */
	open(topicId: string): void {
		this.activeTopic.set(topicId);
		const url = `${this.document.location.origin}${this.getBasePath()}/help-popup?topic=${topicId}`;

		if (this.helpWindow && !this.helpWindow.closed) {
			// Reuse existing window, navigate to new topic
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

		// Track window close
		const checkClosed = setInterval(() => {
			if (!this.helpWindow || this.helpWindow.closed) {
				this.isOpen.set(false);
				this.helpWindow = null;
				clearInterval(checkClosed);
			}
		}, 500);
	}

	/** Closes the help popup window. */
	close(): void {
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
