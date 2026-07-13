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
	/** Display name shown as tooltip */
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
};

export const markdownFormattingActions: MarkdownFormattingAction[] = [
	{
		name: 'Bold',
		icon: 'format_bold',
		markdown: '**bold text**',
		prefix: '**',
		placeholder: 'bold text',
	},
	{
		name: 'Italic',
		icon: 'format_italic',
		markdown: '*italic text*',
		prefix: '*',
		placeholder: 'italic text',
	},
	{
		name: 'Strikethrough',
		icon: 'strikethrough_s',
		markdown: '~~strikethrough~~',
		prefix: '~~',
		placeholder: 'strikethrough',
	},
	{
		name: 'Inline Code',
		icon: 'code',
		markdown: '`code`',
		prefix: '`',
		placeholder: 'code',
	},
	{
		name: 'Link',
		icon: 'link',
		markdown: '[link text](url)',
		prefix: '[',
		suffix: '](url)',
		placeholder: 'link text',
	},
	{
		name: 'Heading',
		icon: 'title',
		markdown: '## Heading',
		prefix: '## ',
		suffix: '',
		placeholder: 'Heading',
	},
	{
		name: 'Bulleted List',
		icon: 'format_list_bulleted',
		markdown: '- Item 1\n- Item 2\n- Item 3',
	},
	{
		name: 'Numbered List',
		icon: 'format_list_numbered',
		markdown: '1. First item\n2. Second item\n3. Third item',
	},
	{
		name: 'Blockquote',
		icon: 'format_quote',
		markdown: '> quote',
		prefix: '> ',
		suffix: '',
		placeholder: 'quote',
	},
	{
		name: 'Code Block',
		icon: 'data_object',
		markdown: '```\ncode block\n```',
		prefix: '```\n',
		suffix: '\n```',
		placeholder: 'code block',
	},
	{
		name: 'Horizontal Rule',
		icon: 'horizontal_rule',
		markdown: '---',
	},
];
