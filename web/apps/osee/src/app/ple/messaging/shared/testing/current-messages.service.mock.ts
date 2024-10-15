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
import { applic } from '@osee/applicability/types';
import { ATTRIBUTETYPEID } from '@osee/attributes/constants';
import { attribute } from '@osee/attributes/types';
import type {
	message,
	settingsDialogData,
	subMessage,
} from '@osee/messaging/shared/types';
import { transactionResultMock } from '@osee/transactions/testing';
import { transactionToken } from '@osee/transactions/types';
import { BehaviorSubject, of, ReplaySubject, Subject } from 'rxjs';
import { CurrentMessagesService } from '../services/ui/current-messages.service';
import { messagesMock } from './messages.response.mock';
import { MimPreferencesMock } from './mim-preferences.response.mock';
import { signal } from '@angular/core';

let sideNavContentPlaceholder = new ReplaySubject<{
	opened: boolean;
	field: string;
	currentValue:
		| string
		| number
		| boolean
		| applic
		| attribute<unknown, ATTRIBUTETYPEID>;
	previousValue?:
		| string
		| number
		| boolean
		| applic
		| undefined
		| attribute<unknown, ATTRIBUTETYPEID>;
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
const diffmode = new BehaviorSubject<boolean>(false);
export const CurrentMessageServiceMock: Partial<CurrentMessagesService> = {
	messageFilter: signal(''),
	messages: of(messagesMock),
	messagesCount: of(10),
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
	get connectionIdSignal() {
		return signal<`${number}`>('123');
	},
	expandedRows: signal([messagesMock[0]]),
	currentPage: new BehaviorSubject(0),
	clearRows() {},
};
