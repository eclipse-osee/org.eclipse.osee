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
import { applic } from '../../../../types/applicability/applic';
import { settingsDialogData } from '../types/settingsdialog';
import { CurrentStructureService } from '../services/ui/current-structure.service';
import { structure } from '../types/structure';
import { platformTypesMock } from './platform-types.response.mock';
import { structuresMock } from './Structures.mock';
import { transactionToken } from '../../../../transactions/transaction';
import { unitsMock } from './unit.response.mock';
import { PlatformType } from '../types/platformType';
import { transactionResultMock } from '../../../../transactions/transaction.mock';
import { MimQuery } from '../types/MimQuery';
import { messagesMock } from './messages.response.mock';
import { element } from '../types/element';
import { transactionResult } from '../../../../types/change-report/transaction';
import { changeInstance } from '../../../../types/change-report/change-report';

let sideNavContentPlaceholder = new ReplaySubject<{
	opened: boolean;
	field: string;
	currentValue: string | number | boolean | applic;
	previousValue?: string | number | boolean | applic | undefined;
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
	changeElementPlatformType(structureId, elementId, typeId) {
		return of(transactionResultMock);
	},
	partialUpdateElement(body, structureId) {
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
	applic: of([
		{ id: '1', name: 'Base' },
		{ id: '2', name: 'Second' },
	]),
	types: of(platformTypesMock),
	get preferences() {
		return of(MimPreferencesMock);
	},
	structures: of(structuresMock),
	branchId: '10',
	messageId: '10',
	subMessageId: '10',
	connection: '10',
	SubMessageId: new BehaviorSubject('10'),
	BranchId: new BehaviorSubject('10'),
	branchType: new BehaviorSubject('working'),
	MessageId: new BehaviorSubject('10'),
	connectionId: new BehaviorSubject('10'),
	units: of(unitsMock),
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
	updatePlatformTypeValue(type: Partial<PlatformType>) {
		return of(transactionResultMock);
	},
	expandedRows: of([]),
	expandedRowsDecreasing: new BehaviorSubject<boolean>(false),
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
		pageNum: string | number
	): Observable<PlatformType[]> {
		throw new Error('Function not implemented.');
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
};
