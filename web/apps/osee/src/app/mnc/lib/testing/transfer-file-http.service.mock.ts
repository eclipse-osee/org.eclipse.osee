/*********************************************************************
 * Copyright (c) 2024 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Carl Wilson
 *     Boeing - initial API and implementation
 **********************************************************************/
import { HttpResponse } from '@angular/common/http';
import { of } from 'rxjs';
import { TransferFileService } from '../../services/transfer-file/transfer-file.service';

const mockTestResultsGetData = {
	title: 'Transfer File: Export Directory Data',
	results: [
		'OSEETransfer-124388928741-20231207140241-4629.zip',
		'OSEETransfer-124388928741-20240129160535-9417.zip',
		'OSEETransfer-124388928741-20240222111420-4359.zip',
		'OSEETransfer-124388928742-20231207122223-3823.zip',
		'OSEETransfer-124388928742-20240221144853-5026.zip',
		'OSEETransfer-124388928743-20240129160713-3888.zip',
		'OSEETransfer-124388928743-20240129161042-9043.zip',
		'OSEETransfer-124388928743-20240221151029-1726.zip',
		'OSEETransfer-124388928743-20240307145747-1980.zip',
		'OSEETransfer-124388928743-20240319155134-7797.zip',
		'OSEETransfer-124388928743-20240319160307-0320.zip',
	],
	ids: ['124388928741', '124388928742', '124388928743'],
	errorCount: 0,
	warningCount: 0,
	infoCount: 11,
	tables: [
		{
			name: 'TFG Data',
			columns: [
				{
					id: 'Source',
					name: 'Source',
					width: 100,
					type: 'String',
				},
				{
					id: 'Date',
					name: 'Date',
					width: 100,
					type: 'String',
				},
				{
					id: 'File',
					name: 'File',
					width: 100,
					type: 'String',
				},
			],
			rows: [
				{
					values: [
						'124388928741',
						'12/07/2023 14:02',
						'OSEETransfer-124388928741-20231207140241-4629.zip',
					],
				},
				{
					values: [
						'124388928741',
						'01/29/2024 16:05',
						'OSEETransfer-124388928741-20240129160535-9417.zip',
					],
				},
				{
					values: [
						'124388928741',
						'02/22/2024 11:14',
						'OSEETransfer-124388928741-20240222111420-4359.zip',
					],
				},
				{
					values: [
						'124388928742',
						'12/07/2023 12:22',
						'OSEETransfer-124388928742-20231207122223-3823.zip',
					],
				},
				{
					values: [
						'124388928742',
						'02/21/2024 14:48',
						'OSEETransfer-124388928742-20240221144853-5026.zip',
					],
				},
				{
					values: [
						'124388928743',
						'01/29/2024 16:07',
						'OSEETransfer-124388928743-20240129160713-3888.zip',
					],
				},
				{
					values: [
						'124388928743',
						'01/29/2024 16:10',
						'OSEETransfer-124388928743-20240129161042-9043.zip',
					],
				},
				{
					values: [
						'124388928743',
						'02/21/2024 15:10',
						'OSEETransfer-124388928743-20240221151029-1726.zip',
					],
				},
				{
					values: [
						'124388928743',
						'03/07/2024 14:57',
						'OSEETransfer-124388928743-20240307145747-1980.zip',
					],
				},
				{
					values: [
						'124388928743',
						'03/19/2024 15:51',
						'OSEETransfer-124388928743-20240319155134-7797.zip',
					],
				},
				{
					values: [
						'124388928743',
						'03/19/2024 16:03',
						'OSEETransfer-124388928743-20240319160307-0320.zip',
					],
				},
			],
		},
	],
	txId: 0,
	numErrors: 0,
	numWarnings: 0,
	success: true,
	warnings: false,
	ok: true,
	errors: false,
	failed: false,
	numErrorsViaSearch: 0,
	numWarningsViaSearch: 0,
	empty: false,
};
const mockTestResultsGetDataWithId = {
	title: 'Transfer File: Export Directory Data',
	results: [
		'OSEETransfer-124388928741-20231207140241-4629.zip',
		'OSEETransfer-124388928741-20240129160535-9417.zip',
		'OSEETransfer-124388928741-20240222111420-4359.zip',
	],
	ids: ['124388928741'],
	errorCount: 0,
	warningCount: 0,
	infoCount: 3,
	tables: [
		{
			name: 'TFG Data',
			columns: [
				{
					id: 'Source',
					name: 'Source',
					width: 100,
					type: 'String',
				},
				{
					id: 'Date',
					name: 'Date',
					width: 100,
					type: 'String',
				},
				{
					id: 'File',
					name: 'File',
					width: 100,
					type: 'String',
				},
			],
			rows: [
				{
					values: [
						'124388928741',
						'12/07/2023 14:02',
						'OSEETransfer-124388928741-20231207140241-4629.zip',
					],
				},
				{
					values: [
						'124388928741',
						'01/29/2024 16:05',
						'OSEETransfer-124388928741-20240129160535-9417.zip',
					],
				},
				{
					values: [
						'124388928741',
						'02/22/2024 11:14',
						'OSEETransfer-124388928741-20240222111420-4359.zip',
					],
				},
			],
		},
	],
	txId: 0,
	numErrors: 0,
	numWarnings: 0,
	success: true,
	warnings: false,
	ok: true,
	errors: false,
	failed: false,
	numErrorsViaSearch: 0,
	numWarningsViaSearch: 0,
	empty: false,
};
const mockTestResultsGenerateExport = {
	title: '',
	results: [
		'Transfer file OSEETransfer-124388928741-20240529153726-0949.zip is successfully generated.',
	],
	ids: ['124388928741'],
	errorCount: 0,
	warningCount: 0,
	infoCount: 1,
	tables: [
		{
			name: 'TFG Data',
			columns: [
				{
					id: 'Source',
					name: 'Source',
					width: 100,
					type: 'String',
				},
				{
					id: 'Date',
					name: 'Date',
					width: 100,
					type: 'String',
				},
				{
					id: 'File',
					name: 'File',
					width: 100,
					type: 'String',
				},
			],
			rows: [
				{
					values: [
						'124388928741',
						'05/29/2024 15:37',
						'OSEETransfer-124388928741-20240529153726-0949.zip',
					],
				},
			],
		},
	],
	txId: 0,
	empty: false,
	failed: false,
	numWarnings: 0,
	numErrors: 0,
	errors: false,
	warnings: false,
	ok: true,
	numErrorsViaSearch: 0,
	numWarningsViaSearch: 0,
	success: true,
};
const mockTestBlob = new Blob(['mock data'], { type: 'text/plain' });
const mockTestResponse = new HttpResponse<Blob>({
	body: mockTestBlob,
	status: 200,
});
export const transferFileHttpServiceMock: Partial<TransferFileService> = {
	exportData: of(mockTestResultsGetData),
	getData() {
		return of(mockTestResultsGetData);
	},
	getDataWithId() {
		return of(mockTestResultsGetDataWithId);
	},
	generateExport() {
		return of(mockTestResultsGenerateExport);
	},
	downloadFile() {
		return of(mockTestResponse);
	},
};
