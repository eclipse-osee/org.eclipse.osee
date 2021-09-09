import { of } from "rxjs";
import { relation, transaction } from "src/app/transactions/transaction";
import { transactionMock } from "src/app/transactions/transaction.mock";
import { response } from "../../../connection-view/mocks/Response.mock";
import { StructuresService } from "../../services/structures.service";
import { structure } from "../../types/structure";
import { structuresMock } from "../ReturnObjects/structure.mock";

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
}