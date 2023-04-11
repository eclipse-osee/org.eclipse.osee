/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { HelpPage } from '@osee/shared/types';
export const helpPagesResponseMock: HelpPage[] = [
	{
		id: '1',
		name: 'Help Header',
		appName: 'APP',
		header: true,
		training: false,
		markdownContent: '',
		children: [
			{
				id: '11',
				name: 'Help Page 1',
				appName: 'APP',
				header: false,
				training: false,
				markdownContent: '# Help Page 1',
				children: [],
			},
		],
	},
];
