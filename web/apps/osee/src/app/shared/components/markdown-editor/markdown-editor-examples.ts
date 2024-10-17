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
export type mdExample = {
	name: string;
	icon: string;
	markdown: string;
};

export const mdExamples: mdExample[] = [
	{
		name: 'Italics',
		icon: 'format_italic',
		markdown: '*Italics*',
	},
	{
		name: 'Bold',
		icon: 'format_bold',
		markdown: '**Bold**',
	},
	{
		name: 'Table',
		icon: 'border_all',
		markdown:
			'|col 1|col 2|col 3|\n|:--|:-:|--:|\n|this is left aligned|this is center aligned|this is right aligned|',
	},
	{
		name: 'Heading 1',
		icon: 'title',
		markdown: '# Heading 1',
	},
	{
		name: 'Heading 2',
		icon: 'title',
		markdown: '## Heading 2',
	},
	{
		name: 'Heading 3',
		icon: 'title',
		markdown: '### Heading 3',
	},
	{
		name: 'Heading 4',
		icon: 'title',
		markdown: '#### Heading 4',
	},
	{
		name: 'Heading 5',
		icon: 'title',
		markdown: '##### Heading 5',
	},
	{
		name: 'Heading 6',
		icon: 'title',
		markdown: '###### Heading 6',
	},
	{
		name: 'Hyperlink',
		icon: 'link',
		markdown:
			'![OSEE Logo](https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSdFmNLYa0Ec0J_V2-uxyo0YPBeL-AnLULpmEQ62min3aka6Gj0qeXkKY3LaIgHVbwxb5U&usqp=CAU)',
	},
	{
		name: 'Code',
		icon: 'code',
		markdown: '```const oseeIsGreat = true;```',
	},
];
