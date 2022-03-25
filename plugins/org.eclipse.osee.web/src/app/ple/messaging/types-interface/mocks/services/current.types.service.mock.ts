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
import { response } from "../../../connection-view/mocks/Response.mock";
import { CurrentTypesService } from "../../services/current-types.service";
import { enumerationSet } from "../../../shared/types/enum";
import { logicalTypeFormDetail } from "../../../shared/types/logicaltype";
import { PlatformType } from "../../../shared/types/platformType";
import { enumerationSetMock } from "../returnObjects/enumerationset.mock";
import { transactionResultMock } from '../../../../../transactions/transaction.mock';

export const currentTypesServiceMock: Partial<CurrentTypesService> = {
    logicalTypes: of([{
        id: '0',
        name: 'enumeration',
        idString: '0',
        idIntValue: 0
    }]),
    getLogicalTypeFormDetail(id: string) {
        return of<logicalTypeFormDetail>({
            id: '0', name: 'enumeration', idIntValue: 0, idString: '0', fields: [
                {
                    name: 'InterfacePlatformTypeBitSize',
                    attributeType: 'InterfacePlatformTypeBitSize',
                    editable: true,
                    required: true,
                    defaultValue:'0'
                },
                {
                    name: 'name',
                    attributeType: 'string',
                    editable: true,
                    required: true,
                    defaultValue:'0'
                }
        ]})
    },
    applic: of([{ id: '1', name: 'Base' }, { id: '2', name: 'Second' }]),
    enumSets: of(enumerationSetMock),
    getEnumSet(platformTypeId: string) {
        return of(enumerationSetMock[0])
    },
    partialUpdate(body: Partial<PlatformType>) {
        return of(transactionResultMock)
    },    
    inEditMode: of(true),
    copyType(body: PlatformType | Partial<PlatformType>) {
        return of(transactionResultMock)
    },
    changeEnumSet(changes: enumerationSet) {
        return of(transactionResultMock)
    },
    updatePreferences(preferences) {
        return of(transactionResultMock)
    }
}