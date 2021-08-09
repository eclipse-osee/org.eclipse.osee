import { of } from "rxjs";
import { CurrentStateService } from "../../services/current-state.service";
import { platformTypesMock } from "../ReturnObjects/PlatformTypes.mock";
import { elementResponseMock } from "../ReturnObjects/response.mock";

export const CurrentStateServiceMock: Partial<CurrentStateService> = {
    changeElementPlatformType(structureId, elementId, typeId) {
        return of(elementResponseMock)
    },
    partialUpdateElement(body, structureId) {
        return of(elementResponseMock)
    },
    partialUpdateStructure(body) {
        return of(elementResponseMock)
    },
    applic: of([{ id: '1', name: 'Base' }, { id: '2', name: 'Second' }]),
    types:of(platformTypesMock),
}