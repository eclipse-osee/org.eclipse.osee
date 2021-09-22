/*********************************************************************
 * Copyright (c) 2021 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatStepper } from '@angular/material/stepper';
import { CurrentStateService } from '../../services/current-state.service';
import { AddElementDialog } from '../../types/AddElementDialog';
import { element } from '../../types/element';

@Component({
  selector: 'osee-messaging-add-element-dialog',
  templateUrl: './add-element-dialog.component.html',
  styleUrls: ['./add-element-dialog.component.sass']
})
export class AddElementDialogComponent implements OnInit {

  availableElements = this.structures.availableElements;
  storedId: string = '-1';
  availableTypes= this.structures.types;
  constructor(private structures:CurrentStateService,public dialogRef: MatDialogRef<AddElementDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: AddElementDialog) { }

  ngOnInit(): void {
  }

  createNew() {
    this.data.element.id = '-1';
   }
  storeId(value: element) {
    this.storedId = value.id || '-1';
  }

  moveToStep(index: number, stepper: MatStepper) {
    stepper.selectedIndex = index - 1;
  }
  moveToReview(stepper: MatStepper) {
    this.data.element.id = this.storedId;
    this.moveToStep(3, stepper);
  }
}
