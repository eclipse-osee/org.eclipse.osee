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
import { changeReportRow } from 'src/app/types/change-report/change-report';

export const changeReportMock: changeReportRow[] = [
	{
		ids: '200141 - 200148',
		names: 'Message1 <---> M1S2',
		itemType: 'Interface Message SubMessage Content',
		itemKind: 'Relation',
		changeType: 'New',
		isValue: '',
		wasValue: '',
	},
	{
		ids: '200148',
		names: 'M1S2',
		itemType: 'Interface SubMessage',
		itemKind: 'Artifact',
		changeType: 'New',
		isValue: '',
		wasValue: '',
	},
	{
		ids: '200142',
		names: 'M1S1',
		itemType: 'Name',
		itemKind: 'Attribute',
		changeType: 'Modified',
		isValue: 'M1S1',
		wasValue: 'SubMessage1',
	},
	{
		ids: '200148',
		names: 'M1S2',
		itemType: 'Name',
		itemKind: 'Attribute',
		changeType: 'New',
		isValue: 'M1S2',
		wasValue: '',
	},
	{
		ids: '200142',
		names: 'M1S1',
		itemType: 'Description',
		itemKind: 'Attribute',
		changeType: 'Modified',
		isValue: 'This is submessage 1 of message 1',
		wasValue: 'This is submessage 1',
	},
	{
		ids: '200148',
		names: 'M1S2',
		itemType: 'Interface Sub Message Number',
		itemKind: 'Attribute',
		changeType: 'New',
		isValue: '2',
		wasValue: '',
	},
	{
		ids: '200148',
		names: 'M1S2',
		itemType: 'Description',
		itemKind: 'Attribute',
		changeType: 'New',
		isValue: 'This is submessage 2 of message 1',
		wasValue: '',
	},
	{
		ids: '200141',
		names: 'Message1',
		itemType: 'Interface Message',
		itemKind: 'Artifact',
		changeType: 'Applicability',
		isValue: 'ROBOT_SPEAKER = SPKR_C',
		wasValue: 'Base',
	},
	{
		ids: '200142',
		names: 'M1S1',
		itemType: 'Interface SubMessage',
		itemKind: 'Artifact',
		changeType: 'Modified',
		isValue: '',
		wasValue: '',
	},
];
