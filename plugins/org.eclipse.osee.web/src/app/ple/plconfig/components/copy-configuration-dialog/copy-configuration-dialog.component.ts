import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSelectChange } from '@angular/material/select';
import { Observable } from 'rxjs';
import { PlConfigBranchService } from '../../services/pl-config-branch-service.service';
import { PlConfigApplicUIBranchMapping } from '../../types/pl-config-applicui-branch-mapping';
import { PLEditConfigData } from '../../types/pl-edit-config-data';

@Component({
  selector: 'plconfig-copy-configuration-dialog',
  templateUrl: './copy-configuration-dialog.component.html',
  styleUrls: ['./copy-configuration-dialog.component.sass']
})
export class CopyConfigurationDialogComponent implements OnInit {
  branchApplicability: Observable<PlConfigApplicUIBranchMapping>;
  constructor(public dialogRef: MatDialogRef<CopyConfigurationDialogComponent>,@Inject(MAT_DIALOG_DATA) public data: PLEditConfigData,private branchService: PlConfigBranchService) {
    this.branchApplicability = this.branchService.getBranchApplicability(data.currentBranch);
   }

  ngOnInit(): void {
  }
  selectDestinationBranch(event: MatSelectChange) {
    this.data.currentConfig = event.value;
  }
  selectBranch(event: MatSelectChange) {
    this.data.copyFrom = event.value;
  }
  onNoClick(): void {
    this.dialogRef.close();
  }

}
