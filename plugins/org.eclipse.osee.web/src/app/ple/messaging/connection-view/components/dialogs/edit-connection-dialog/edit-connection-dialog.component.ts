import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { applic } from 'src/app/ple/messaging/shared/types/NamedId.applic';
import { CurrentGraphService } from '../../../services/current-graph.service';
import { connection } from '../../../types/connection';

@Component({
  selector: 'app-edit-connection-dialog',
  templateUrl: './edit-connection-dialog.component.html',
  styleUrls: ['./edit-connection-dialog.component.sass']
})
export class EditConnectionDialogComponent implements OnInit {

  title: string = "";
  applics = this.graphService.applic;
  constructor (public dialogRef: MatDialogRef<EditConnectionDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: connection, private graphService: CurrentGraphService) {
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
