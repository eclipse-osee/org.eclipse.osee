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
import { Injectable, signal } from '@angular/core';
import { HelpSection, HelpTopic } from './help-drawer.service';

/**
 * Registry of all help topics in the application.
 * Components register their help topics here so the drawer
 * can look up markdown paths and section metadata.
 */
@Injectable({
	providedIn: 'root',
})
export class HelpTopicRegistryService {
	private readonly topicMap = signal<Map<string, HelpTopic>>(
		new Map(
			defaultTopics.map((t) => [t.id, t])
		)
	);

	/** Returns the topic for the given ID, or undefined if not registered. */
	getTopic(id: string): HelpTopic | undefined {
		return this.topicMap().get(id);
	}

	/** Returns all registered topics. */
	getAllTopics(): HelpTopic[] {
		return [...this.topicMap().values()];
	}

	/** Registers a new topic at runtime (e.g., from a lazy-loaded module). */
	register(topic: HelpTopic): void {
		this.topicMap.update((m) => {
			const copy = new Map(m);
			copy.set(topic.id, topic);
			return copy;
		});
	}
}

/**
 * Default help topics that ship with the application.
 * Each topic maps to a static markdown file in assets/help/.
 */
const markdownEditorSections: HelpSection[] = [
	{ id: 'toolbar', label: 'Toolbar', anchorId: 'md-editor-toolbar' },
	{ id: 'formatting', label: 'Formatting', anchorId: 'md-editor-textarea' },
	{ id: 'images', label: 'Images', anchorId: 'md-editor-image-btn' },
	{ id: 'tables', label: 'Tables', anchorId: 'md-editor-table-btn' },
	{ id: 'preview', label: 'Preview', anchorId: 'md-editor-preview' },
	{ id: 'fullscreen', label: 'Fullscreen', anchorId: 'md-editor-fullscreen-btn' },
];

const tableDialogSections: HelpSection[] = [
	{ id: 'table-size', label: 'Table Size', anchorId: 'table-dialog-dimensions' },
	{ id: 'headers-spans', label: 'Headers & Spans', anchorId: 'table-dialog-headers' },
	{ id: 'editing-cells', label: 'Editing Cells', anchorId: 'table-dialog-cells' },
	{ id: 'column-alignment', label: 'Column Alignment', anchorId: 'table-dialog-alignment' },
	{ id: 'captions', label: 'Captions', anchorId: 'table-dialog-caption' },
	{ id: 'undo-redo', label: 'Undo & Redo', anchorId: 'table-dialog-undo' },
];

const defaultTopics: HelpTopic[] = [
	{
		id: 'markdown-editor',
		label: 'Markdown Editor',
		markdownPath: 'assets/help/markdown-editor/overview.md',
		sections: markdownEditorSections,
	},
	{
		id: 'markdown-table-dialog',
		label: 'Table Editor',
		markdownPath: 'assets/help/markdown-table-dialog/overview.md',
		sections: tableDialogSections,
	},
];
