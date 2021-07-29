import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { RemovalDialog } from '../../../types/ConfirmRemovalDialog';

@Component({
  selector: 'app-confirm-removal-dialog',
  templateUrl: './confirm-removal-dialog.component.html',
  styleUrls: ['./confirm-removal-dialog.component.sass']
})
export class ConfirmRemovalDialogComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<ConfirmRemovalDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: RemovalDialog) { }

  ngOnInit(): void {
  }

  onNoClick() {
    this.dialogRef.close();
  }
}
