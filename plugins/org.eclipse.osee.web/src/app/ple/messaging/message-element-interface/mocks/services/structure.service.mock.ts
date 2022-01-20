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
import { of } from "rxjs";
import { relation, transaction } from "src/app/transactions/transaction";
import { transactionMock } from "src/app/transactions/transaction.mock";
import { response } from "../../../connection-view/mocks/Response.mock";
import { StructuresService } from "../../services/structures.service";
import { structure } from "../../types/structure";
import { structuresMock, structuresMock2 } from "../ReturnObjects/structure.mock";

//let i= 0;
export const structureServiceMock: Partial<StructuresService> = {
    getFilteredStructures(filter: string, branchId: string, messageId: string, subMessageId: string, connectionId: string) {
        return of(structuresMock)
    },
    createSubMessageRelation(subMessageId: string) {
        return of({
            typeName: "Interface SubMessage Content",
            sideA: '10'
        });
    },
    createStructure(body: Partial<structure>, branchId: string, relations: relation[]) {
        return of(transactionMock);
    },
    performMutation(branchId: string, messageId: string, subMessageId: string, connectionId: string, transaction: transaction) {
        return of(response);
    },
    changeStructure(body: Partial<structure>, branchId: string) {
        return of(transactionMock);
    },
    getStructure(branchId: string, messageId: string, subMessageId: string, structureId: string, connectionId: string) {
        return of(structuresMock[0]);
    },
    addRelation(branchId: string, relation: relation, transaction?: transaction) {
        return of(transactionMock);
    },
    deleteSubmessageRelation(branchId: string, submessageId: string, structureId: string) {
        return of(transactionMock);
    },
    deleteStructure(branchId: string, structureId: string) {
        return of(transactionMock);
    }
}
export const structureServiceMock3: Partial<StructuresService> & { _oldStructure: structure[] } = {
    _oldStructure:JSON.parse(JSON.stringify(structuresMock2)) as structure[],
    getFilteredStructures(filter: string, branchId: string, messageId: string, subMessageId: string, connectionId: string) {
        return of(structuresMock)
    },
    createSubMessageRelation(subMessageId: string) {
        return of({
            typeName: "Interface SubMessage Content",
            sideA: '10'
        });
    },
    createStructure(body: Partial<structure>, branchId: string, relations: relation[]) {
        return of(transactionMock);
    },
    performMutation(branchId: string, messageId: string, subMessageId: string, connectionId: string, transaction: transaction) {
        return of(response);
    },
    changeStructure(body: Partial<structure>, branchId: string) {
        return of(transactionMock);
    },
    getStructure(branchId: string, messageId: string, subMessageId: string, structureId: string, connectionId: string) {
        return of(structuresMock[0]);
    },
    addRelation(branchId: string, relation: relation, transaction?: transaction) {
        return of(transactionMock);
    },
    deleteSubmessageRelation(branchId: string, submessageId: string, structureId: string) {
        return of(transactionMock);
    },
    deleteStructure(branchId: string, structureId: string) {
        return of(transactionMock);
    }
}
export const structureServiceRandomMock: Partial<StructuresService> & {i:number} = {
    i: 0,
    getFilteredStructures(filter: string, branchId: string, messageId: string, subMessageId: string, connectionId: string) {
        this.i++;
        return this.i%2?of(structuresMock):of(structuresMock2)
    },
    createSubMessageRelation(subMessageId: string) {
        return of({
            typeName: "Interface SubMessage Content",
            sideA: '10'
        });
    },
    createStructure(body: Partial<structure>, branchId: string, relations: relation[]) {
        return of(transactionMock);
    },
    performMutation(branchId: string, messageId: string, subMessageId: string, connectionId: string, transaction: transaction) {
        return of(response);
    },
    changeStructure(body: Partial<structure>, branchId: string) {
        return of(transactionMock);
    },
    getStructure(branchId: string, messageId: string, subMessageId: string, structureId: string, connectionId: string) {
        return of(structuresMock[0]);
    },
    addRelation(branchId: string, relation: relation, transaction?: transaction) {
        return of(transactionMock);
    },
    deleteSubmessageRelation(branchId: string, submessageId: string, structureId: string) {
        return of(transactionMock);
    },
    deleteStructure(branchId: string, structureId: string) {
        return of(transactionMock);
    }
}