import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { MatSelectChange } from '@angular/material/select';

import { Observable } from 'rxjs';
import { catchError, share, skipWhile, switchMap } from 'rxjs/operators';
import { PlConfigBranchService } from '../../services/pl-config-branch-service.service';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { PlConfigUIStateService } from '../../services/pl-config-uistate.service';
import { PlConfigBranchListingBranch } from '../../types/pl-config-branch';

@Component({
  selector: 'plconfig-branch-selector',
  templateUrl: './branch-selector.component.html',
  styleUrls: ['./branch-selector.component.sass']
})
export class BranchSelectorComponent implements OnInit {
  @Output() selectedBranch = new EventEmitter<number>();
  selectedBranchType: Observable<string> = this.uiStateService.viewBranchType;
  options: Observable<PlConfigBranchListingBranch[]> = this.currentBranchService.branchListing;
  loading = this.uiStateService.loading;
  selectedBranchState = "";
  constructor(private uiStateService: PlConfigUIStateService, private branchService: PlConfigBranchService, private currentBranchService: PlConfigCurrentBranchService) {
    this.uiStateService.branchId.subscribe((branchId) => {
      this.selectedBranchState = branchId.toString();
    })
   }

  ngOnInit(): void {
  }
  selectBranch(event: MatSelectChange) {
    this.selectedBranch.emit(event.value);
    this.uiStateService.updateReqConfig = true;
  }

}
