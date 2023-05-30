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
import { of, BehaviorSubject, ReplaySubject } from 'rxjs';
import { CurrentGraphService } from '../services/current-graph.service';
import { applic } from '@osee/shared/types/applicability';
import { changeInstance } from '@osee/shared/types/change-report';
import type { node, connection } from '@osee/messaging/shared/types';
import { transactionResultMock } from '@osee/shared/transactions/testing';
import { MimPreferencesMock } from '@osee/messaging/shared/testing';
import { changeReportMock } from '@osee/shared/testing';

let sideNavContentPlaceholder = new ReplaySubject<{
	opened: boolean;
	field: string;
	currentValue: string | number | applic | boolean;
	previousValue?: string | number | applic | boolean;
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
	getPaginatedNodes(pageNum, pageSize) {
		return of([
			{ id: '1', name: 'First' },
			{ id: '2', name: 'Second' },
		]);
	},
	updateConnection(connection: Partial<connection>) {
		return of(transactionResultMock);
	},
	unrelateConnection(nodeId: string, id: string) {
		return of(transactionResultMock);
	},
	updateNode(node: Partial<node>) {
		return of(transactionResultMock);
	},
	deleteNodeAndUnrelate(nodeId: string, edges: []) {
		return of(transactionResultMock);
	},
	createNewConnection(
		connection: connection,
		sourceId: string,
		targetId: string
	) {
		return of(transactionResultMock);
	},
	createNewNode(node: node) {
		return of(transactionResultMock);
	},
	nodeOptions: of([
		{ id: '1', name: 'First' },
		{ id: '2', name: 'Second' },
	]),
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
