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
import { BehaviorSubject, of, ReplaySubject, Subject } from "rxjs";
import { MimPreferencesMock } from "../../../shared/mocks/MimPreferences.mock";
import { applic } from "../../../../../types/applicability/applic";
import { settingsDialogData } from "../../../shared/types/settingsdialog";
import { CurrentStructureService } from "../../services/current-structure.service";
import { structure } from "../../../shared/types/structure";
import { platformTypesMock } from "../../../shared/mocks/PlatformTypes.mock";
import { elementResponseMock } from "../ReturnObjects/response.mock";
import { structuresMock } from "../../../shared/mocks/Structures.mock";
import { transactionToken } from "src/app/transactions/transaction";
import { unitsMock } from "../../../shared/mocks/unit.mock";
import { response } from "../../../connection-view/mocks/Response.mock";
import { PlatformType } from "../../../shared/types/platformType";
import { transactionResultMock } from '../../../../../transactions/transaction.mock';

let sideNavContentPlaceholder = new ReplaySubject<{  opened: boolean; field: string; currentValue: string | number | boolean | applic; previousValue?: string | number | boolean | applic | undefined; transaction?: transactionToken | undefined; user?: string | undefined; date?: string | undefined; }>();
sideNavContentPlaceholder.next({opened:false,field:'',currentValue:''})
export const CurrentStateServiceMock: Partial<CurrentStructureService> = {
    createStructure(body: Partial<structure>) {
        return of(transactionResultMock)
    },
    changeElementPlatformType(structureId, elementId, typeId) {
        return of(transactionResultMock)
    },
    partialUpdateElement(body, structureId) {
        return of(transactionResultMock)
    },
    partialUpdateStructure(body) {
        return of(transactionResultMock)
    },
    relateStructure(structureId: string) {
        return of(transactionResultMock)
    },
    updatePreferences(preferences: settingsDialogData) {
        return of(transactionResultMock)
    },
    removeStructureFromSubmessage(structureId: string, submessageId: string) {
        return of(transactionResultMock);
    },
    deleteStructure(structureId: string) {
        return of(transactionResultMock);
    },
    addExpandedRow: {} as structure,
    removeExpandedRow: {} as structure,
    done:new Subject(),
    applic: of([{ id: '1', name: 'Base' }, { id: '2', name: 'Second' }]),
    types: of(platformTypesMock),
    preferences: of(MimPreferencesMock),
    structures: of(structuresMock),
    branchId:"10",
    messageId: "10",
    subMessageId: "10",
    connection: "10",
    SubMessageId:new BehaviorSubject("10"),
    BranchId: new BehaviorSubject("10"),
    branchType: new BehaviorSubject("working"),
    MessageId: new BehaviorSubject("10"),
    connectionId: new BehaviorSubject("10"),
    units: of(unitsMock),
    getStructure(structureId: string) {
        return of(structuresMock[0])
    },
    getStructureRepeating(structureId: string) {
        return of(structuresMock[0])
    },
    sideNavContent: sideNavContentPlaceholder,
    set sideNav(value: { opened: boolean, field: string, currentValue: string | number | applic, previousValue?: string | number | applic, user?: string, date?: string }) {
        
    },
    isInDiff: new BehaviorSubject<boolean>(false),
    updatePlatformTypeValue(type: Partial<PlatformType>) {
        return of(transactionResultMock)
    },
    expandedRows: of([]),
    expandedRowsDecreasing:new BehaviorSubject<boolean>(false)
}