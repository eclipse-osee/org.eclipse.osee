import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { from } from 'rxjs';
import { filter, scan, share, switchMap } from 'rxjs/operators';
import { CurrentGraphService } from '../../../services/current-graph.service';
import { newConnection, transportType } from '../../../types/connection';
import { node } from '../../../types/node';

@Component({
  selector: 'app-create-connection-dialog',
  templateUrl: './create-connection-dialog.component.html',
  styleUrls: ['./create-connection-dialog.component.sass']
})
export class CreateConnectionDialogComponent implements OnInit {

  nodes = this.graphService.nodeOptions.pipe(
    switchMap((nodeList) => from(nodeList).pipe(
      filter((node)=>node.id!==this.data.id)
    )),
    scan((acc, curr) => [...acc, curr], [] as node[]),
    share()
  );
  title: string = "";
  newConnection: newConnection = {
    nodeId: '',
    connection: {
      name: '',
      transportType:transportType.Ethernet
    }
  }
  constructor (private graphService: CurrentGraphService, public dialogRef: MatDialogRef<CreateConnectionDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: node) {
    this.title = data.name;
   }

  ngOnInit(): void {
  }

  onNoClick() {
    this.dialogRef.close();
  }

}
