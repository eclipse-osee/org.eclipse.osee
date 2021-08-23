import { of } from "rxjs";
import { PlConfigCurrentBranchService } from "../services/pl-config-current-branch.service";
import { testBranchApplicability } from "./mockBranchService";
import { testBranchActions, testCommitResponse, testDataPlConfigBranchListingBranch, testWorkFlow } from "./mockTypes";

export const plCurrentBranchServiceMock: Partial<PlConfigCurrentBranchService> = {
    
    
          get branchState() {
            return of(testDataPlConfigBranchListingBranch)
          },
          get branchAction() {
            return of(testBranchActions);
          },  
          commitBranch(parentBranchId: string | number | undefined, body: { committer: string, archive: string }) {    
            return of(testCommitResponse);
          },
          get branchApplicability() {
            return of(testBranchApplicability);
          },
          get branchWorkFlow() {
            return of(testWorkFlow);
          }
           
          }