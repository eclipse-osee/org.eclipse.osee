import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatMenuTrigger } from '@angular/material/menu';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute, Router } from '@angular/router';
import { iif } from 'rxjs';
import { forkJoin, of } from 'rxjs';
import { filter, first, map, mergeMap, switchMap, take } from 'rxjs/operators';
import { CurrentStateService } from '../../services/current-state.service';
import { AddElementDialog } from '../../types/AddElementDialog';
import { AddElementDialogComponent } from '../add-element-dialog/add-element-dialog.component';

@Component({
  selector: 'ple-messaging-message-element-interface-sub-element-table',
  templateUrl: './sub-element-table.component.html',
  styleUrls: ['./sub-element-table.component.sass']
})
export class SubElementTableComponent implements OnInit, OnChanges {
  @Input() data: any = {};
  @Input() dataSource: MatTableDataSource<any> = new MatTableDataSource<any>();
  @Input() filter: string = "";
  
  @Input() element: any = {};
  @Output() expandRow = new EventEmitter();
  @Input() subMessageHeaders: string[] = [];
  private _branchId: string = "";
  private _branchType: string = "";
  @Input() editMode: boolean = false;
  menuPosition = {
    x: '0',
    y:'0'
  }
  @ViewChild(MatMenuTrigger, { static: true })
  matMenuTrigger!: MatMenuTrigger;
  constructor(private route: ActivatedRoute, private router: Router, public dialog: MatDialog, private structureService: CurrentStateService) {
    this.subMessageHeaders = ["name", "beginWord", "endWord", "BeginByte", "EndByte",  "interfaceElementAlterable", "description", "notes"];
    this.dataSource.data = this.data;
  }
  ngOnChanges(changes: SimpleChanges): void {
    this.dataSource.data = this.data;
    if (this.filter !== "") {
      this.dataSource.filter = this.filter.replace('element: ', '');
      this.filter = this.filter.replace('element: ', "");
      if (this.dataSource.filteredData.length > 0) {
        this.expandRow.emit(this.element);
      }
    }
  }

  ngOnInit(): void {
    this.dataSource.data = this.data;
    if (this.filter !== "") {
      this.dataSource.filter = this.filter.replace('element: ','');
    }

    this.route.paramMap.subscribe((values) => {
      this._branchId = values.get("branchId") || '';
      this._branchType = values.get("branchType") || '';
    })
  }

  valueTracker(index: any, item: any) {
    return index;
  }

  navigateTo(location: string) {
    this.router.navigate([this._branchType,this._branchId,"types",location], {
      relativeTo: this.route.parent?.parent,
      queryParamsHandling: 'merge',
    });
  }

  openMenu(event: MouseEvent,location: string) {
    event.preventDefault();
    this.menuPosition.x = event.clientX + 'px';
    this.menuPosition.y = event.clientY + 'px';
    this.matMenuTrigger.menuData = {
      location:location
    }
    this.matMenuTrigger.openMenu();
  }
  navigateToInNewTab(location: string) {
    const url = this.router.serializeUrl(this.router.createUrlTree([this._branchType,this._branchId,"types", location], {
      relativeTo: this.route.parent?.parent,
      queryParamsHandling: 'merge',
    }))
    window.open(url, "_blank");
  }

  openAddElementDialog() {
    let dialogData: AddElementDialog = {
      id: this.element?.id||'',
      name: this.element?.name||'',
      element: {
        id: '-1',
        name: '',
        description: '',
        notes: '',
        interfaceElementAlterable: true,
        interfaceElementIndexEnd: 0,
        interfaceElementIndexStart: 0,
      },
      type:{id:'',name:''}
    }
    let dialogRef = this.dialog.open(AddElementDialogComponent, {
      data:dialogData
    });
    let createElement = dialogRef.afterClosed().pipe(
      filter((val) => (val !== undefined ||val!==null) && val?.element!==undefined),
      switchMap((value:AddElementDialog) =>
        iif(() => value.element.id !== '-1' && value.element.id.length > 0 && value.type.id!==''&&value.type.name!=='',
          this.structureService.relateElement(this.element.id, value.element.id),
          this.structureService.createNewElement(value.element, this.element.id,value.type.id))
      ),
      take(1)
    );
    createElement.subscribe();
  }
  
}
