import { of } from "rxjs";
import { ApplicabilityListService } from "../services/http/applicability-list.service";

export const applicabilityListServiceMock: Partial<ApplicabilityListService> = {
    getApplicabilities(branchId: string | number) {
        return of([{id:'1',name:'Base'},{id:'2',name:'Second'}])
    }
}