/*********************************************************************
 * Copyright (c) 2022 Boeing
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
export const INSERTIONS = new Map([
	[
		'TABLE',
		`|Header_1|Header_2|Header_3|
|:-----------:|:-----------:|:-----------:|
|    r1c1    |    r1c2    |    r1c3    |
|    r2c1    |    r2c2    |    r2c3    |
|    r3c1    |    r3c2    |    r3c3    |`,
	],
	['HEADING_1', '# '],
	['HEADING_2', '## '],
	['HEADING_3', '### '],
	['HEADING_4', '#### '],
	['HEADING_5', '##### '],
	['HEADING_6', '###### '],
	['IMAGE', '![IMAGE_ALT_TEXT](IMAGE_URL)'],
	[
		'OSEE_LOGO',
		'![OSEE Logo](https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSdFmNLYa0Ec0J_V2-uxyo0YPBeL-AnLULpmEQ62min3aka6Gj0qeXkKY3LaIgHVbwxb5U&usqp=CAU)',
	],
]);
