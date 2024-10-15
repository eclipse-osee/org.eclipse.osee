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
import { MimPreferencesMock, nodesMock } from '@osee/messaging/shared/testing';
import type { connection, nodeData } from '@osee/messaging/shared/types';
import { changeReportMock } from '@osee/shared/testing';
import { applic, applicabilitySentinel } from '@osee/applicability/types';
import { changeInstance } from '@osee/shared/types/change-report';
import { transactionResultMock } from '@osee/transactions/testing';
import { BehaviorSubject, ReplaySubject, of } from 'rxjs';
import { CurrentGraphService } from '../services/current-graph.service';
import { attribute } from '@osee/attributes/types';
import { ATTRIBUTETYPEID } from '@osee/attributes/constants';

let sideNavContentPlaceholder = new ReplaySubject<{
	opened: boolean;
	field: string;
	currentValue:
		| string
		| number
		| applic
		| boolean
		| attribute<unknown, ATTRIBUTETYPEID>;
	previousValue?:
		| string
		| number
		| applic
		| boolean
		| attribute<unknown, ATTRIBUTETYPEID>;
	user?: string;
	date?: string;
}>();
sideNavContentPlaceholder.next({ opened: true, field: '', currentValue: '' });
export const graphServiceMock: Partial<CurrentGraphService> = {
	nodes: of({ nodes: [], edges: [], clusters: [] }),
	updated: new BehaviorSubject<boolean>(true),
	set update(value: boolean) {
		return;
	},
	updateConnection(connection: Partial<connection>) {
		return of(transactionResultMock);
	},
	unrelateConnection(nodeIds: `${number}`[], connectionId: `${number}`) {
		return of(transactionResultMock);
	},
	updateNode(node: Partial<nodeData>) {
		return of(transactionResultMock);
	},
	deleteNodeAndUnrelate(nodeId: string) {
		return of(transactionResultMock);
	},
	createNewConnection(connection: connection) {
		return of(transactionResultMock);
	},
	createNewNode(node: nodeData) {
		return of(transactionResultMock);
	},
	preferences: of(MimPreferencesMock),
	InDiff: new BehaviorSubject<boolean>(true),
	differences: new BehaviorSubject<changeInstance[] | undefined>(
		changeReportMock
	),
	sideNavContent: sideNavContentPlaceholder,
	set sideNav(value: {
		opened: boolean;
		field: string;
		currentValue: string | number | applic;
		previousValue?: string | number | applic;
		user?: string;
		date?: string;
	}) {},
	get messageRoute() {
		return of({
			beginning: '/ple/messaging/' + 'working' + '/' + '8' + '/',
			end: '/diff',
		});
	},
};
