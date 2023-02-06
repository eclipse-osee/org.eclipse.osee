/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import { BehaviorSubject, of, ReplaySubject, Subject } from 'rxjs';
import { MimPreferencesMock } from './mim-preferences.response.mock';
import { applic } from '../../../../types/applicability/applic';
import { CurrentMessagesService } from '../services/ui/current-messages.service';
import {
	message,
	messageWithChanges,
	settingsDialogData,
	subMessage,
} from '@osee/messaging/shared/types';
import { transactionToken } from '@osee/shared/transactions';
import { transactionResultMock } from '@osee/shared/transactions/testing';

let sideNavContentPlaceholder = new ReplaySubject<{
	opened: boolean;
	field: string;
	currentValue: string | number | boolean | applic;
	previousValue?: string | number | boolean | applic | undefined;
	transaction?: transactionToken | undefined;
	user?: string | undefined;
	date?: string | undefined;
}>();
sideNavContentPlaceholder.next({
	opened: true,
	field: '',
	currentValue: '',
	previousValue: '',
});
let expectedData: (message | messageWithChanges)[] = [
	{
		id: '10',
		name: 'name',
		description: 'description',
		interfaceMessageRate: '50Hz',
		interfaceMessageNumber: '0',
		interfaceMessagePeriodicity: '1Hz',
		interfaceMessageWriteAccess: true,
		interfaceMessageType: 'Connection',
		subMessages: [
			{
				id: '5',
				name: 'sub message name',
				description: '',
				interfaceSubMessageNumber: '0',
				applicability: {
					id: '1',
					name: 'Base',
				},
			},
		],
		applicability: {
			id: '1',
			name: 'Base',
		},
		initiatingNode: {
			id: '1',
			name: 'Node 1',
		},
		changes: {
			name: {
				previousValue: '',
				currentValue: 'name',
				transactionToken: {
					id: '-1',
					branchId: '-1',
				},
			},
		},
	},
];
const diffmode = new BehaviorSubject<boolean>(false);
export const CurrentMessageServiceMock: Partial<CurrentMessagesService> = {
	messages: of(expectedData),
	applic: of([
		{ id: '1', name: 'Base' },
		{ id: '2', name: 'Second' },
	]),
	partialUpdateSubMessage(body, messageId) {
		return of(transactionResultMock);
	},
	partialUpdateMessage(body) {
		return of(transactionResultMock);
	},
	createMessage(body: message) {
		return of(transactionResultMock);
	},
	BranchId: new BehaviorSubject('10'),
	preferences: of(MimPreferencesMock),
	updatePreferences(preferences: settingsDialogData) {
		return of(transactionResultMock);
	},
	removeMessage(messageId: string) {
		return of(transactionResultMock);
	},
	removeSubMessage(subMessageId: string, messageId: string) {
		return of(transactionResultMock);
	},
	relateSubMessage(messageId: string, subMessageId: string) {
		return of(transactionResultMock);
	},
	createSubMessage(body: subMessage, messageId: string) {
		return of(transactionResultMock);
	},
	deleteMessage(messageId: string) {
		return of(transactionResultMock);
	},
	deleteSubMessage(subMessageId: string) {
		return of(transactionResultMock);
	},
	done: new Subject(),
	isInDiff: diffmode,
	sideNavContent: sideNavContentPlaceholder,
	set sideNav(value: {
		opened: boolean;
		field: string;
		currentValue: string | number | boolean | applic;
		previousValue?: string | number | boolean | applic | undefined;
		transaction?: transactionToken | undefined;
		user?: string | undefined;
		date?: string | undefined;
	}) {},
	get initialRoute() {
		return of(
			'/ple/messaging/' +
				'working' +
				'/' +
				'10' +
				'/' +
				'20' +
				'/messages/'
		);
	},
	get endOfRoute() {
		return of('');
	},
	expandedRows: of([
		{
			id: '1',
			name: 'dummy element',
			description: 'description',
		} as message,
	]),
	expandedRowsDecreasing: new BehaviorSubject<boolean>(false),
};
