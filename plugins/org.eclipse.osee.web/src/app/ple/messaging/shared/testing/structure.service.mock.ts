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
import type { structure } from '@osee/messaging/shared/types';
import { relation, transaction } from '@osee/shared/types';
import {
	transactionMock,
	transactionResultMock,
} from '@osee/shared/transactions/testing';
import { of } from 'rxjs';
import { StructuresService } from '../services/http/structures.service';
import { structuresMock3, structuresMock2 } from './structure.mock';

//let i= 0;
export const structureServiceMock: Partial<StructuresService> = {
	getFilteredStructures(
		filter: string,
		branchId: string,
		messageId: string,
		subMessageId: string,
		connectionId: string,
		viewId: string,
		pageNum: number,
		pageSize: number
	) {
		return of(structuresMock3);
	},
	createSubMessageRelation(subMessageId: string) {
		return of({
			typeName: 'Interface SubMessage Content',
			sideA: '10',
		});
	},
	createStructure(
		body: Partial<structure>,
		branchId: string,
		relations: relation[]
	) {
		return of(transactionMock);
	},
	performMutation(transaction: transaction) {
		return of(transactionResultMock);
	},
	changeStructure(body: Partial<structure>, branchId: string) {
		return of(transactionMock);
	},
	getStructure(
		branchId: string,
		messageId: string,
		subMessageId: string,
		structureId: string,
		connectionId: string
	) {
		return of(structuresMock3[0]);
	},
	addRelation(
		branchId: string,
		relation: relation,
		transaction?: transaction
	) {
		return of(transactionMock);
	},
	deleteSubmessageRelation(
		branchId: string,
		submessageId: string,
		structureId: string
	) {
		return of(transactionMock);
	},
	deleteStructure(branchId: string, structureId: string) {
		return of(transactionMock);
	},
};
export const structureServiceMock3: Partial<StructuresService> & {
	_oldStructure: structure[];
} = {
	_oldStructure: JSON.parse(JSON.stringify(structuresMock2)) as structure[],
	getFilteredStructures(
		filter: string,
		branchId: string,
		messageId: string,
		subMessageId: string,
		connectionId: string,
		viewId: string,
		pageNum: number,
		pageSize: number
	) {
		return of(structuresMock3);
	},
	createSubMessageRelation(subMessageId: string) {
		return of({
			typeName: 'Interface SubMessage Content',
			sideA: '10',
		});
	},
	createStructure(
		body: Partial<structure>,
		branchId: string,
		relations: relation[]
	) {
		return of(transactionMock);
	},
	performMutation(transaction: transaction) {
		return of(transactionResultMock);
	},
	changeStructure(body: Partial<structure>, branchId: string) {
		return of(transactionMock);
	},
	getStructure(
		branchId: string,
		messageId: string,
		subMessageId: string,
		structureId: string,
		connectionId: string
	) {
		return of(structuresMock3[0]);
	},
	addRelation(
		branchId: string,
		relation: relation,
		transaction?: transaction
	) {
		return of(transactionMock);
	},
	deleteSubmessageRelation(
		branchId: string,
		submessageId: string,
		structureId: string
	) {
		return of(transactionMock);
	},
	deleteStructure(branchId: string, structureId: string) {
		return of(transactionMock);
	},
};
export const structureServiceRandomMock: Partial<StructuresService> & {
	i: number;
} = {
	i: 0,
	getFilteredStructures(
		filter: string,
		branchId: string,
		messageId: string,
		subMessageId: string,
		connectionId: string,
		viewId: string,
		pageNum: number,
		pageSize: number
	) {
		this.i++;
		return this.i % 2 ? of(structuresMock3) : of(structuresMock2);
	},
	createSubMessageRelation(subMessageId: string) {
		return of({
			typeName: 'Interface SubMessage Content',
			sideA: '10',
		});
	},
	createStructure(
		body: Partial<structure>,
		branchId: string,
		relations: relation[]
	) {
		return of(transactionMock);
	},
	performMutation(transaction: transaction) {
		return of(transactionResultMock);
	},
	changeStructure(body: Partial<structure>, branchId: string) {
		return of(transactionMock);
	},
	getStructure(
		branchId: string,
		messageId: string,
		subMessageId: string,
		structureId: string,
		connectionId: string
	) {
		return of(structuresMock3[0]);
	},
	addRelation(
		branchId: string,
		relation: relation,
		transaction?: transaction
	) {
		return of(transactionMock);
	},
	deleteSubmessageRelation(
		branchId: string,
		submessageId: string,
		structureId: string
	) {
		return of(transactionMock);
	},
	deleteStructure(branchId: string, structureId: string) {
		return of(transactionMock);
	},
	getStructures(branchId: string) {
		return of(structuresMock3);
	},
};
