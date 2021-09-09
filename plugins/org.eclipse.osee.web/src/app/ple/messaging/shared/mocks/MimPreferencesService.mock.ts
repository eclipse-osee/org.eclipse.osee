import { of } from "rxjs";
import { user } from "src/app/userdata/types/user-data-user";
import { MimPreferencesService } from "../services/http/mim-preferences.service";
import { MimPreferencesMock } from "./MimPreferences.mock";

export const MimPreferencesServiceMock: Partial<MimPreferencesService> = {
    getUserPrefs(branchId: string, user: user) {
        return of(MimPreferencesMock)
    },
    getBranchPrefs(user: user) {
        return of(['10:false','8:true'])
    }
}