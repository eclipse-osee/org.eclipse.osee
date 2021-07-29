import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { node } from '../../../types/node';

@Component({
  selector: 'app-edit-node-dialog',
  templateUrl: './edit-node-dialog.component.html',
  styleUrls: ['./edit-node-dialog.component.sass']
})
export class EditNodeDialogComponent implements OnInit {

  title:string=""
  constructor (public dialogRef: MatDialogRef<EditNodeDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: node) {
    this.title = data.name;
   }

  ngOnInit(): void {
  }

  onNoClick() {
    this.dialogRef.close();
  }
}
