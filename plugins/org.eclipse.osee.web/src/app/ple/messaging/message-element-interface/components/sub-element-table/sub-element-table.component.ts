/*********************************************************************
 * Copyright (c) 2021 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatMenuTrigger } from '@angular/material/menu';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute, Router } from '@angular/router';
import { iif, of } from 'rxjs';
import { filter, switchMap, take } from 'rxjs/operators';
import { LayoutNotifierService } from 'src/app/layoutNotification/layout-notifier.service';
import { CurrentStateService } from '../../services/current-state.service';
import { AddElementDialog } from '../../types/AddElementDialog';
import { element } from '../../types/element';
import { RemoveElementDialogData } from '../../types/RemoveElementDialog';
import { structure } from '../../types/structure';
import { AddElementDialogComponent } from '../add-element-dialog/add-element-dialog.component';
import { RemoveElementDialogComponent } from '../remove-element-dialog/remove-element-dialog.component';

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
  layout = this.layoutNotifier.layout;
  menuPosition = {
    x: '0',
    y:'0'
  }
  editableElementHeaders: string[] = [
    'name',
    'platformTypeName2',
    'interfaceElementAlterable',
    'description',
    'notes',
    'applicability',
    'interfaceElementIndexStart',
    'interfaceElementIndexEnd',
  ];

  @ViewChild('generalMenuTrigger', { static: true })
  generalMenuTrigger!: MatMenuTrigger;
  constructor(private route: ActivatedRoute, private router: Router, public dialog: MatDialog, private structureService: CurrentStateService,private layoutNotifier: LayoutNotifierService) {
    this.subMessageHeaders = ["name", "beginWord", "endWord", "BeginByte", "EndByte",  "interfaceElementAlterable", "description", "notes"];
    this.dataSource.data = this.data;
  }
  ngOnChanges(changes: SimpleChanges): void {
    if (Array.isArray(this.data)) {
      this.dataSource.data = this.data; 
    }
    if (this.filter !== "") {
      this.dataSource.filter = this.filter.replace('element: ', '');
      this.filter = this.filter.replace('element: ', "");
      if (this.dataSource.filteredData.length > 0) {
        this.expandRow.emit(this.element);
      }
    }
  }

  ngOnInit(): void {
    if (Array.isArray(this.data)) {
      this.dataSource.data = this.data; 
    }
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
        iif(() => value.element.id !== '-1' && value.element.id.length > 0,
          this.structureService.relateElement(this.element.id, value.element.id),
          this.structureService.createNewElement(value.element, this.element.id,value.type.id))
      ),
      take(1)
    );
    createElement.subscribe();
  }
  openGeneralMenu(event: MouseEvent, element: element) {
    event.preventDefault();
    this.menuPosition.x = event.clientX + 'px';
    this.menuPosition.y = event.clientY + 'px';
    this.generalMenuTrigger.menuData = {
      element: element,
      structure:this.element
    }
    this.generalMenuTrigger.openMenu();
  }

  removeElement(element: element,structure:structure) {
    const dialogData: RemoveElementDialogData = {
      elementId: element.id,
      structureId: structure.id,
      elementName:element.name
    }
    this.dialog.open(RemoveElementDialogComponent, {
      data:dialogData
    }).afterClosed().pipe(
      take(1),
      switchMap((dialogResult: string) => iif(() => dialogResult === 'ok',
        this.structureService.removeElementFromStructure(element, structure),
        of()
      ))
    ).subscribe()
  }
  deleteElement(element: element) {
    //open dialog, yes/no if yes -> this.structures.deleteElement()
    const dialogData: RemoveElementDialogData = {
      elementId: element.id,
      structureId: '',
      elementName:element.name
    }
    this.dialog.open(RemoveElementDialogComponent, {
      data:dialogData
    }).afterClosed().pipe(
      take(1),
      switchMap((dialogResult: string) => iif(() => dialogResult === 'ok',
        this.structureService.deleteElement(element),
        of()
      ))
    ).subscribe()
  }
}
