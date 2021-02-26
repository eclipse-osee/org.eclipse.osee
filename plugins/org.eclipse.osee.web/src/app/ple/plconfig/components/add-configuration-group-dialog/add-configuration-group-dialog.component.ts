import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { addCfgGroup } from '../../types/pl-config-cfggroups';

@Component({
  selector: 'app-add-configuration-group-dialog',
  templateUrl: './add-configuration-group-dialog.component.html',
  styleUrls: ['./add-configuration-group-dialog.component.sass']
})
export class AddConfigurationGroupDialogComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<AddConfigurationGroupDialogComponent>,@Inject(MAT_DIALOG_DATA) public data: addCfgGroup) { }

  ngOnInit(): void {
  }
  
  onNoClick(): void {
    this.dialogRef.close();
  }
}
