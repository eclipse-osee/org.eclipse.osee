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
import { transaction } from "src/app/transactions/transaction";
import { transactionMock } from "src/app/transactions/transaction.mock";
import { response } from "../../../connection-view/mocks/Response.mock";
import { platformTypes1 } from "../../../type-element-search/testing/MockResponses/PlatformType";
import { TypesService } from "../../services/types.service";
import { PlatformType } from "../../types/platformType";
import { logicalTypeMock } from "../returnObjects/logicalType.mock";
import { logicalTypeFormDetailMock } from "../returnObjects/logicalTypeFormDetail.mock";

export const typesServiceMock: Partial<TypesService> = {
    performMutation(body: transaction, branchId: string) {
        return of(response);
    },
    getFilteredTypes(filter: string, branchId: string) {
        return of(platformTypes1);
    },
    changePlatformType(branchId: string, type: Partial<PlatformType>) {
        return of(transactionMock);
    },
    createPlatformType(branchId: string, type: PlatformType | Partial<PlatformType>) {
        return of(transactionMock);
    },
    getLogicalTypeFormDetail(id: string) {
        return of(logicalTypeFormDetailMock);
    },
    logicalTypes:of(logicalTypeMock)
}