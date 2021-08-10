import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { node } from '../../../../shared/types/node';

@Component({
  selector: 'app-create-new-node-dialog',
  templateUrl: './create-new-node-dialog.component.html',
  styleUrls: ['./create-new-node-dialog.component.sass']
})
export class CreateNewNodeDialogComponent implements OnInit {

  result: node = {
    name: '',
    description:''
  };
  constructor(public dialogRef: MatDialogRef<CreateNewNodeDialogComponent>) { }

  ngOnInit(): void {
  }

  onNoClick() {
    this.dialogRef.close();
  }

}
