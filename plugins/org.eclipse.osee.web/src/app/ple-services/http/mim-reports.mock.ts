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
import { MimReport } from '@osee/messaging/shared/types';
import { FileExtensions, ProducesMediaType } from 'src/app/types/files';
import { HttpMethods } from 'src/app/types/http-methods';
export const mimReportsMock: MimReport[] = [
	{
		id: '1',
		name: 'Test Report',
		url: '/test/test',
		httpMethod: HttpMethods.GET,
		fileExtension: FileExtensions.XML,
		fileNamePrefix: 'Prefix',
		producesMediaType: ProducesMediaType.XML,
		diffAvailable: true,
	},
];
