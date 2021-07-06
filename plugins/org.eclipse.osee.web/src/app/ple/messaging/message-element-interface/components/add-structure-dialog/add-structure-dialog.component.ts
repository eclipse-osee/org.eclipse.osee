import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatStepper } from '@angular/material/stepper';
import { EnumsService } from '../../../shared/services/http/enums.service';
import { CurrentStateService } from '../../services/current-state.service';
import { AddStructureDialog } from '../../types/AddStructureDialog';
import { structure } from '../../types/structure';

@Component({
  selector: 'osee-messaging-add-structure-dialog',
  templateUrl: './add-structure-dialog.component.html',
  styleUrls: ['./add-structure-dialog.component.sass']
})
export class AddStructureDialogComponent implements OnInit {

  availableStructures = this.structures.availableStructures;
  categories = this.enumService.categories;
  storedId:string='-1'
  constructor(private structures:CurrentStateService,public dialogRef: MatDialogRef<AddStructureDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: AddStructureDialog, private enumService: EnumsService) { }

  ngOnInit(): void {
  }

  moveToStep(index: number, stepper: MatStepper) {
    stepper.selectedIndex = index - 1;
  }

  createNew() {
    this.data.structure.id = '-1';
  }

  storeId(value: structure) {
    this.storedId = value.id || '-1';
  }

  moveToReview(stepper: MatStepper) {
    this.data.structure.id = this.storedId;
    this.moveToStep(3, stepper);
  }
}
