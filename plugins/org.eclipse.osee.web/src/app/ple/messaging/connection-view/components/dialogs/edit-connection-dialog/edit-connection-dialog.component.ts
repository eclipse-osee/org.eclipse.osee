import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { connection } from '../../../types/connection';

@Component({
  selector: 'app-edit-connection-dialog',
  templateUrl: './edit-connection-dialog.component.html',
  styleUrls: ['./edit-connection-dialog.component.sass']
})
export class EditConnectionDialogComponent implements OnInit {

  title:string=""
  constructor (public dialogRef: MatDialogRef<EditConnectionDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: connection) {
    this.title = data.name;
  }

  ngOnInit(): void {
  }

  onNoClick() {
    this.dialogRef.close();
  }
}
