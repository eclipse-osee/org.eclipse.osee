import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { PlConfigUIStateService } from './services/pl-config-uistate.service';

@Component({
  selector: 'app-plconfig',
  templateUrl: './plconfig.component.html',
  styleUrls: ['./plconfig.component.sass']
})
export class PlconfigComponent implements OnInit {
  _updateRequired: Observable<boolean>;
  _branchType: string = '';
  _loading: Observable<string>;
  constructor(private uiStateService: PlConfigUIStateService, private route: ActivatedRoute, private router: Router) {
    this._updateRequired = this.uiStateService.updateReq;
    this._loading = this.uiStateService.loading;
    this.uiStateService.viewBranchType.subscribe((id) => {
      this._branchType = id;
    })
   }

  ngOnInit(): void {
    this.route.paramMap.subscribe((values) => {
      this.uiStateService.branchIdNum = values.get('branchId') || '';
      this.uiStateService.viewBranchTypeString=values.get('branchType')||'';
    })
  }
  branchTypeSelected(branchType:string): void {
    this.uiStateService.viewBranchTypeString = branchType;
    this.uiStateService.branchIdNum = '';
    this.router.navigate([branchType], {
      relativeTo: this.route.parent,
      queryParamsHandling: 'merge',
    })
  }
  branchSelected(branch: number): void{
    this.uiStateService.branchIdNum = branch.toString();
    this.router.navigate([this._branchType,branch], {
      relativeTo: this.route.parent,
      queryParamsHandling: 'merge',
    })
  }

}
