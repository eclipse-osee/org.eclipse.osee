import { of } from "rxjs";
import { EditAuthService } from "../../shared/services/edit-auth-service.service";
import { branchApplicability } from "../../shared/types/branch.applic";

export const editAuthServiceMock: Partial<EditAuthService> = {
    get branchEditability() {
        return of<branchApplicability>({
            associatedArtifactId: '-1',
            branch: {
              id: '-1',
              viewId: '-1',
              idIntValue: -1,
              name: '',
            },
            editable: false,
            features: [],
            groups: [],
            parentBranch: {
              id: '-1',
              viewId: '-1',
              idIntValue: -1,
              name: '',
            },
            views: [],
          })
    }
}