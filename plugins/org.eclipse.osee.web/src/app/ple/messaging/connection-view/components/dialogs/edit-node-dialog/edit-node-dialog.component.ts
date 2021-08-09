import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { applic } from 'src/app/ple/messaging/shared/types/NamedId.applic';
import { CurrentGraphService } from '../../../services/current-graph.service';
import { node } from '../../../types/node';

@Component({
  selector: 'app-edit-node-dialog',
  templateUrl: './edit-node-dialog.component.html',
  styleUrls: ['./edit-node-dialog.component.sass']
})
export class EditNodeDialogComponent implements OnInit {

  title: string = "";
  applics = this.graphService.applic;
  constructor (public dialogRef: MatDialogRef<EditNodeDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: node, private graphService: CurrentGraphService) {
    this.title = data.name;
   }

  ngOnInit(): void {
  }

  onNoClick() {
    this.dialogRef.close();
  }

  compareApplics(o1:applic,o2:applic) {
    return o1?.id === o2?.id && o1?.name === o2?.name;
  }
}
