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

import {
	ResponseColumnSchema,
	ResponseTableData,
} from '../types/grid-commander-types/table-data-types';

//mock data in anticipated structure that will be fetched from API
export const columnDataMock: ResponseColumnSchema[] = [
	{
		name: 'name',
		type: 'text',
	},
	{
		name: 'description',
		type: 'text',
	},
	{
		name: 'url',
		type: 'text',
	},
	{
		name: 'type',
		type: 'text',
	},
	{
		name: 'permissions',
		type: 'text',
	},
	{
		name: 'icon',
		type: 'text',
	},
];

export const artifactAttributesByRowMock: string[][] = [
	[
		'test1',
		'this is a test1',
		'test@test1.com',
		'number',
		'permissionType1',
		'1!',
	],
	[
		'test2',
		'this is a test2',
		'test@test2.com',
		'number',
		'permissionType2',
		'2@',
	],
	[
		'test3',
		'this is a test',
		'test@test3.com',
		'number',
		'permissionType3',
		'3#',
	],
	[
		'test4',
		'this is a test4',
		'test@test4.com',
		'number',
		'permissionType1',
		'&!',
	],
	[
		'test5',
		'this is a test5',
		'test@test5.com',
		'number',
		'permissionType2',
		'2@',
	],
	[
		'test6',
		'this is a test',
		'test@test6.com',
		'number',
		'permissionType3',
		'3#',
	],
	[
		'test112',
		'this to test filter',
		'test1234@testing.com',
		'number',
		'permissionType12',
		'1!',
	],
	[
		'test21',
		'this is a test21!!',
		'test21@testingData.com',
		'number',
		'permissionType212',
		'2@',
	],
	[
		'test31',
		'this is a test',
		'test31!!@test.com',
		'number',
		'permissionType31',
		'3#',
	],
	[
		'test41',
		'this is a test41!',
		'test41Data@test.com',
		'number',
		'permissionType41',
		'&!',
	],
	[
		'test512',
		'this is a test512',
		'test51@test2.com',
		'number',
		'permissionType512',
		'2@',
	],
	[
		'test61',
		'this is test61 data',
		'test61!@test.com',
		'number',
		'permissionType3',
		'3#',
	],
	[
		'test1!1',
		'this is a test1!1',
		'testdata!1!@test.com',
		'number',
		'permissionType1!',
		'1!',
	],
	[
		'test22',
		'this is a test2@2',
		'test22#@test2.com',
		'number',
		'permissionType22#',
		'2@',
	],
	[
		'test33!@',
		'this is test33!@',
		'testDataMock3@test.com',
		'number',
		'permissionTypeUnknown',
		'3#',
	],
	[
		'test414',
		'this is test4141',
		'test414@test1.com',
		'number',
		'permissionType4',
		'&!',
	],
	[
		'test55',
		'this is a test5',
		'test@test2.com',
		'number',
		'permissionType2',
		'2@',
	],
	[
		'wednesday',
		'is a weekday',
		'notFriday@weekday.com',
		'weekday',
		'all',
		'7',
	],
	[
		'test464',
		'this is a testTest4',
		'testData@test41.com',
		'number',
		'permissionType4',
		'&!',
	],
	[
		'test55',
		'this is a test5',
		'test@test2.com',
		'number',
		'permissionType2',
		'2@',
	],
	[
		'friday',
		'the end of the week',
		'betterthanmonday@days.com',
		'weekday',
		'all',
		'7',
	],
];

export const COMBINED_TABLE_DATA: ResponseTableData = {
	tableOptions: {
		//colummn metadata
		columns: columnDataMock,
	},
	//data that will be inserted into data table -- order of array data should correspond to the order the columns will be displayed
	data: artifactAttributesByRowMock,
};
