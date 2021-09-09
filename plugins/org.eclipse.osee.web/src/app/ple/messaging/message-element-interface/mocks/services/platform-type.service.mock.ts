import { of } from "rxjs";
import { PlatformTypeService } from "../../services/platform-type.service";
import { platformTypesMock } from "../ReturnObjects/PlatformTypes.mock";

export const platformTypeServiceMock: Partial<PlatformTypeService> = {
    getType(branchId: string, typeId: string) {
        return of(platformTypesMock[0])
    },
    getTypes(branchId: string) {
        return of(platformTypesMock)
    }
}