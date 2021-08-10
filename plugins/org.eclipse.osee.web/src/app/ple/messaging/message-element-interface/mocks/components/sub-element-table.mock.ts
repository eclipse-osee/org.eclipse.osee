import { EventEmitter, Output } from "@angular/core";
import { Component, Input } from "@angular/core";
import { MatTableDataSource } from "@angular/material/table";

@Component({
    selector: 'ple-messaging-message-element-interface-sub-element-table',
    template:'<p>Dummy</p>'
})
export class SubElementTableComponentMock{
  @Input() data: any = {};
  @Input() dataSource: MatTableDataSource<any> = new MatTableDataSource<any>();
  @Input() filter: string = "";
  
  @Input() element: any = {};
  @Output() expandRow = new EventEmitter();
  @Input() subMessageHeaders: string[] = [];
  @Input() editMode: boolean = false;
  }