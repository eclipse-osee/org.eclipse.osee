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
import { BehaviorSubject, Observable, of, ReplaySubject, Subject } from 'rxjs';
import { MimPreferencesMock } from './mim-preferences.response.mock';
import { applic } from '@osee/applicability/types';
import { CurrentStructureService } from '../services/ui/current-structure.service';
import { platformTypesMock } from './platform-types.response.mock';
import { structuresMock } from './Structures.mock';
import { messagesMock } from './messages.response.mock';
import { changeInstance } from '@osee/shared/types/change-report';
import type {
	structure,
	settingsDialogData,
	PlatformType,
	element,
} from '@osee/messaging/shared/types';
import { transactionResult, transactionToken } from '@osee/transactions/types';
import { transactionResultMock } from '@osee/transactions/testing';
import type { MimQuery } from '@osee/messaging/shared/query';
import { signal } from '@angular/core';
import { ATTRIBUTETYPEID } from '@osee/attributes/constants';
import { attribute } from '@osee/attributes/types';

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
let _singleStructureId = new BehaviorSubject<string>('10');
sideNavContentPlaceholder.next({ opened: false, field: '', currentValue: '' });
export const CurrentStateServiceMock: Partial<CurrentStructureService> = {
	createStructure(body: Partial<structure>) {
		return of(transactionResultMock);
	},
	partialUpdateElement(body) {
		return of(transactionResultMock);
	},
	partialUpdateStructure(body) {
		return of(transactionResultMock);
	},
	relateStructure(structureId: string) {
		return of(transactionResultMock);
	},
	updatePreferences(preferences: settingsDialogData) {
		return of([
			transactionResultMock,
			transactionResultMock,
			transactionResultMock,
			transactionResultMock,
		]);
	},
	removeStructureFromSubmessage(structureId: string, submessageId: string) {
		return of(transactionResultMock);
	},
	deleteStructure(structureId: string) {
		return of(transactionResultMock);
	},
	removeElementFromStructure(element: element, structure: structure) {
		return of(transactionResultMock);
	},
	deleteElement(element: element) {
		return of(transactionResultMock);
	},
	addExpandedRow: {} as structure,
	removeExpandedRow: {} as structure,
	done: new Subject(),
	types: of(platformTypesMock),
	get preferences() {
		return of(MimPreferencesMock);
	},
	structures: of(structuresMock),
	branchId: '10',
	messageId: '10',
	subMessageId: '10',
	connection: '10',
	structuresCount: of(10),
	SubMessageId: signal('10'),
	BranchId: new BehaviorSubject('10'),
	branchType: new BehaviorSubject<'working' | 'baseline' | ''>('working'),
	MessageId: new BehaviorSubject('10'),
	connectionId: new BehaviorSubject('10'),
	getType(typeId: string) {
		return of(platformTypesMock[0]);
	},
	getPaginatedElements(pageNum: string | number) {
		return of([]);
	},
	sideNavContent: sideNavContentPlaceholder,
	set sideNav(value: {
		opened: boolean;
		field: string;
		currentValue: string | number | applic;
		previousValue?: string | number | applic;
		user?: string;
		date?: string;
	}) {},
	isInDiff: new BehaviorSubject<boolean>(false),
	expandedRows: signal([]),
	structureFilter: signal(''),
	singleStructureId: _singleStructureId,
	set singleStructureIdValue(value: string) {
		_singleStructureId.next(value);
	},
	message: of(messagesMock[0]),
	connectionsRoute: of(''),
	query: function <T = unknown>(
		query: MimQuery<T>
	): Observable<Required<T>[]> {
		return of<Required<T>[]>([{ name: 'abcd' } as unknown as Required<T>]);
	},
	getPaginatedStructures: function (
		pageNum: string | number
	): Observable<Required<structure>[]> {
		throw new Error('Function not implemented.');
	},
	availableElements: undefined,
	getPaginatedFilteredTypes: function (
		filter: string,
		count: number,
		pageNum: string | number
	): Observable<PlatformType[]> {
		return of(platformTypesMock);
	},
	getFilteredTypesCount(filter: string) {
		return of(10);
	},
	set difference(id: changeInstance[]) {},
	DiffMode: false,
	createNewElement: function (
		body: Partial<element>,
		structureId: string,
		typeId: string,
		afterElement?: string | undefined
	): Observable<transactionResult> {
		return of(transactionResultMock);
	},
	relateElement: function (
		structureId: string,
		elementId: string,
		afterElement?: string | undefined
	): Observable<transactionResult> {
		return of(transactionResultMock);
	},
	currentPageSize: of(10),
	currentPage: of(1),
	clearRows: function () {},
};
