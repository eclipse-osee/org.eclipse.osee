import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSelectChange } from '@angular/material/select';
import { Observable, of } from 'rxjs';
import { PlConfigBranchService } from '../../services/pl-config-branch-service.service';
import { PlConfigTypesService } from '../../services/pl-config-types.service';
import { PlConfigApplicUIBranchMapping } from '../../types/pl-config-applicui-branch-mapping';
import { PLAddFeatureData, writeFeature } from '../../types/pl-config-features';

@Component({
  selector: 'plconfig-add-feature-dialog',
  templateUrl: './add-feature-dialog.component.html',
  styleUrls: ['./add-feature-dialog.component.sass']
})
export class AddFeatureDialogComponent implements OnInit {
  branchApplicability: Observable<PlConfigApplicUIBranchMapping>;
  productApplicabilities: Observable<string[]>;
  private _valueTypes: string[] = ['String', 'Integer', 'Decimal', 'Boolean'];
  valueTypes: Observable<string[]>= of(this._valueTypes);
  constructor(public dialogRef: MatDialogRef<AddFeatureDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: PLAddFeatureData, private branchService: PlConfigBranchService, private typeService:PlConfigTypesService){ 
    this.branchApplicability = this.branchService.getBranchApplicability(data.currentBranch);
    this.productApplicabilities = this.typeService.productApplicabilityTypes;
  }

  ngOnInit(): void {
  }
  onNoClick(): void {
    this.dialogRef.close();
  }
  selectMultiValued() {
    this.autoSetValueIfBoolean();
  }
  autoSetValueIfBoolean() {
    if (this.data.feature.valueType === 'Boolean' && !this.data.feature.multiValued && this.data.feature.values.length<=2) {
      this.data.feature.values = ['Included', 'Excluded'];
    }
  }
  increaseValueArray() {
    this.data.feature.values.length = this.data.feature.values.length + 1;
  }
  selectDefaultValue(event: MatSelectChange) {
    this.data.feature.defaultValue = event.value;
  }
  valueTracker(index: any, item: any) {
    return index;
  }
  clearFeatureData() {
    this.data.feature = new writeFeature();
  }
}
