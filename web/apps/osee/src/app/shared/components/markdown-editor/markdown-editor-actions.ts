/*********************************************************************
 * Copyright (c) 2024 Boeing
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

/**
 * A formatting action that can either wrap selected text (inline)
 * or insert a block-level markdown snippet at the cursor.
 *
 * - Inline actions use `prefix`/`suffix` to wrap text (e.g. **bold**).
 * - Block actions use `markdown` for direct insertion (e.g. a list).
 */
export type MarkdownFormattingAction = {
	/** Display name shown in the panel */
	name: string;
	/** Material icon name */
	icon: string;
	/**
	 * Full markdown to insert when no wrapping behavior applies.
	 * Also used as fallback for block-level insertions.
	 */
	markdown: string;
	/** Prefix to wrap selected text (inline formatting) */
	prefix?: string;
	/** Suffix to wrap selected text. Defaults to prefix if omitted. */
	suffix?: string;
	/** Placeholder text inserted when no text is selected */
	placeholder?: string;
	/** Visual group for layout in the toolbar */
	group: 'inline' | 'block' | 'media';
};

export const markdownFormattingActions: MarkdownFormattingAction[] = [
	{
		name: 'Bold',
		icon: 'format_bold',
		markdown: '**bold text**',
		prefix: '**',
		placeholder: 'bold text',
		group: 'inline',
	},
	{
		name: 'Italic',
		icon: 'format_italic',
		markdown: '*italic text*',
		prefix: '*',
		placeholder: 'italic text',
		group: 'inline',
	},
	{
		name: 'Strikethrough',
		icon: 'strikethrough_s',
		markdown: '~~strikethrough~~',
		prefix: '~~',
		placeholder: 'strikethrough',
		group: 'inline',
	},
	{
		name: 'Inline Code',
		icon: 'code',
		markdown: '`code`',
		prefix: '`',
		placeholder: 'code',
		group: 'block',
	},
	{
		name: 'Link',
		icon: 'link',
		markdown: '[link text](url)',
		prefix: '[',
		suffix: '](url)',
		placeholder: 'link text',
		group: 'block',
	},
	{
		name: 'Blockquote',
		icon: 'format_quote',
		markdown: '> quote',
		prefix: '> ',
		suffix: '',
		placeholder: 'quote',
		group: 'block',
	},
	{
		name: 'Code Block',
		icon: 'data_object',
		markdown: '```\ncode block\n```',
		prefix: '```\n',
		suffix: '\n```',
		placeholder: 'code block',
		group: 'block',
	},
	{
		name: 'Horizontal Rule',
		icon: 'horizontal_rule',
		markdown: '---',
		group: 'block',
	},
	{
		name: 'Figure Caption',
		icon: 'video_label',
		markdown: '<figure-caption>caption text</figure-caption>',
		prefix: '<figure-caption>',
		suffix: '</figure-caption>',
		placeholder: 'caption text',
		group: 'media',
	},
	{
		name: 'Table Caption',
		icon: 'legend_toggle',
		markdown: '<table-caption>caption text</table-caption>',
		prefix: '<table-caption>',
		suffix: '</table-caption>',
		placeholder: 'caption text',
		group: 'media',
	},
];

export const headingLevels = [1, 2, 3, 4, 5, 6] as const;
export type HeadingLevel = (typeof headingLevels)[number];

export const headingOptions: SplitButtonCompatibleOption[] = [
	{ value: '1', name: 'Heading 1', icon: 'looks_one' },
	{ value: '2', name: 'Heading 2', icon: 'looks_two' },
	{ value: '3', name: 'Heading 3', icon: 'looks_3' },
	{ value: '4', name: 'Heading 4', icon: 'looks_4' },
	{ value: '5', name: 'Heading 5', icon: 'looks_5' },
	{ value: '6', name: 'Heading 6', icon: 'looks_6' },
];

/**
 * Minimal shape compatible with SplitButtonOption.
 */
export type SplitButtonCompatibleOption = {
	value: string;
	name: string;
	icon: string;
};

export type ListOption = {
	name: string;
	value: string;
	icon: string;
	markdown: string;
};

export const listOptions: ListOption[] = [
	{
		name: 'Bulleted List',
		value: 'bulleted',
		icon: 'format_list_bulleted',
		markdown: '- Item 1\n- Item 2\n- Item 3',
	},
	{
		name: 'Numbered List',
		value: 'numbered',
		icon: 'format_list_numbered',
		markdown: '1. First item\n2. Second item\n3. Third item',
	},
	{
		name: 'Task List',
		value: 'task',
		icon: 'checklist',
		markdown: '- [ ] Task 1\n- [ ] Task 2\n- [x] Completed task',
	},
];
