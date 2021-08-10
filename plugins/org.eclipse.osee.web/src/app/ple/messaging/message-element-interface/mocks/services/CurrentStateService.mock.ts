import { BehaviorSubject, of } from "rxjs";
import { CurrentStateService } from "../../services/current-state.service";
import { structure } from "../../types/structure";
import { platformTypesMock } from "../ReturnObjects/PlatformTypes.mock";
import { elementResponseMock } from "../ReturnObjects/response.mock";
import { structuresMock } from "../ReturnObjects/Structures.mock";

export const CurrentStateServiceMock: Partial<CurrentStateService> = {
    createStructure(body: Partial<structure>) {
        return of(elementResponseMock)
    },
    changeElementPlatformType(structureId, elementId, typeId) {
        return of(elementResponseMock)
    },
    partialUpdateElement(body, structureId) {
        return of(elementResponseMock)
    },
    partialUpdateStructure(body) {
        return of(elementResponseMock)
    },
    relateStructure(structureId: string) {
        return of(elementResponseMock)
    },
    applic: of([{ id: '1', name: 'Base' }, { id: '2', name: 'Second' }]),
    types:of(platformTypesMock),
    structures: of(structuresMock),
    branchId:"10",
    messageId: "10",
    subMessageId: "10",
    connection: "10",
    SubMessageId:new BehaviorSubject("10"),
    BranchId:new BehaviorSubject("10")
}