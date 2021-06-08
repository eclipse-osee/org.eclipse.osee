import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatMenuTrigger } from '@angular/material/menu';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute, Router } from '@angular/router';
import { iif } from 'rxjs';
import { filter, switchMap, tap } from 'rxjs/operators';
import { CurrentMessagesService } from '../../services/current-messages.service';
import { AddSubMessageDialog } from '../../types/AddSubMessageDialog';
import { message } from '../../types/messages';
import { subMessage } from '../../types/sub-messages';
import { AddSubMessageDialogComponent } from './add-sub-message-dialog/add-sub-message-dialog.component';

@Component({
  selector: 'ple-messaging-sub-message-table',
  templateUrl: './sub-message-table.component.html',
  styleUrls: ['./sub-message-table.component.sass']
})
export class SubMessageTableComponent implements OnInit, OnChanges {
  @Input() data: subMessage[] = [];
  @Input() dataSource: MatTableDataSource<subMessage> = new MatTableDataSource<subMessage>();
  @Input() filter: string = "";
  
  @Input() element!: message;
  @Input() editMode: boolean = false;
  @Output() expandRow = new EventEmitter();
  headers: string[] = [];
  menuPosition = {
    x: '0',
    y:'0'
  }
  @ViewChild(MatMenuTrigger, { static: true })
  matMenuTrigger!: MatMenuTrigger;
  constructor(public dialog: MatDialog,private route: ActivatedRoute, private router: Router,private messageService: CurrentMessagesService) {
    this.dataSource.data = this.data;
    this.headers=["name","description","interfaceSubMessageNumber","interfaceMessageRate", " "]
  }
  ngOnChanges(changes: SimpleChanges): void {
    this.dataSource.data = this.data;
    if (this.filter !== "") {
      if (this.dataSource.filteredData.length > 0) {
        this.expandRow.emit(this.element);
      } 
    }
  }

  ngOnInit(): void {
  }
  valueTracker(index: any, item: any) {
    return index;
  }

  navigateToElementsTable(id: string | undefined, submessage: string, location: string) {
    this.router.navigate([id,submessage,location, 'elements'], {
      relativeTo: this.route,
      queryParamsHandling: 'merge',
    })
  }

  openMenu(event: MouseEvent, id: string | undefined, submessage: string, location: string) {
    event.preventDefault();
    this.menuPosition.x = event.clientX + 'px';
    this.menuPosition.y = event.clientY + 'px';
    this.matMenuTrigger.menuData = {
      id: id,
      submessage: submessage,
      location:location
    }
    this.matMenuTrigger.openMenu();
  }
  navigateToElementsTableInNewTab(id: string | undefined, submessage: string, location: string) {
    const url = this.router.serializeUrl(this.router.createUrlTree([id,submessage,location, 'elements'], {
      relativeTo: this.route,
      queryParamsHandling: 'merge',
    }))
    window.open(url, "_blank");
  }

  createNewSubMessage() {
    const dialogRef = this.dialog.open(AddSubMessageDialogComponent, {
      data: {
        name:this.element.name,
        id: this.element.id,
        subMessage: {
          name: '',
          description: '',
          interfaceMessageRate: '',
          interfaceSubMessageNumber:''
        }
      }
    })
    dialogRef.afterClosed().pipe(
      filter((val)=>val!==undefined),
      switchMap((z: AddSubMessageDialog) => iif(() => z != undefined && z.subMessage != undefined && z.subMessage.id != undefined && z?.subMessage?.id.length > 0 && z.subMessage.id!=='-1', this.messageService.relateSubMessage(z.id, z?.subMessage?.id || '-1'), this.messageService.createSubMessage(z.subMessage, z.id)))
    ).subscribe();
  }

}
