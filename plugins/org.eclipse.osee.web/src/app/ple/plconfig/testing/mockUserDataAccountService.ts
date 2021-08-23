import { of } from "rxjs";
import { UserDataAccountService } from "src/app/userdata/services/user-data-account.service";
import { testDataUser } from "./mockTypes";

export const userDataAccountServiceMock: Partial<UserDataAccountService> = {
    getUser() {
        return of(testDataUser);
    }
  }