import { BreakpointObserver, Breakpoints, BreakpointState } from '@angular/cdk/layout';
import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { BehaviorSubject, combineLatest } from 'rxjs';
import { take, switchMap } from 'rxjs/operators';
import { ColumnPreferencesDialogComponent } from '../../../shared/components/dialogs/column-preferences-dialog/column-preferences-dialog.component';
import { CurrentTypesService } from '../../services/current-types.service';
import { PlMessagingTypesUIService } from '../../services/pl-messaging-types-ui.service';
import { PlatformType } from '../../types/platformType';
import { NewTypeDialogComponent } from '../new-type-dialog/new-type-dialog.component';

@Component({
  selector: 'ple-messaging-types-type-grid',
  templateUrl: './type-grid.component.html',
  styleUrls: ['./type-grid.component.sass']
})
export class TypeGridComponent implements OnInit, OnChanges {
  @Input() filterValue: string = "";
  columnCount= this.uiService.columnCount;
  gutterSize: string = "";
  filteredData = this.typesService.typeData;
  rowHeight: string = "";
  
  constructor(private breakpointObserver: BreakpointObserver, private typesService: CurrentTypesService, private uiService: PlMessagingTypesUIService,public dialog: MatDialog) {
    this.uiService.filterString = this.filterValue;
    const breakpoint =this.breakpointObserver.observe(
      [
        Breakpoints.XSmall,
        Breakpoints.Small,
        Breakpoints.Medium,
        Breakpoints.Large,
        Breakpoints.XLarge,
        Breakpoints.Web,
      ]
    )
    const combined = combineLatest([breakpoint, this.uiService.singleLineAdjustment]).subscribe((result) => {
      this.updateColumnsCount(result);
    })
  }
  
  ngOnChanges(changes: SimpleChanges): void {
    this.uiService.filterString = this.filterValue;
  }

  ngOnInit(): void {
  }

  /**
   * Adjusts the layout of the page based on CDK Layout Observer
   * @param state Array containing the state of the page (i.e. what breakpoints) and whether or not to adjust the layout due to being on a single line
   */
  updateColumnsCount(state: [BreakpointState,number]) {
    if (state[0].matches) {
      if (state[0].breakpoints[Breakpoints.XSmall]) {
        this.uiService.columnCountNumber = 1;
        this.gutterSize = "16";
      }
      if (state[0].breakpoints[Breakpoints.Small]) {
        this.uiService.columnCountNumber = 2;
        this.gutterSize = "16";
      }
      if (state[0].breakpoints[Breakpoints.Medium]) {
        this.rowHeight = 45 + state[1] + "%";
        //this.rowHeight="30%"
        this.uiService.columnCountNumber = 3;
        this.gutterSize = "24";
      }
      if (state[0].breakpoints[Breakpoints.Large] && !state[0].breakpoints[Breakpoints.Medium]) {
        this.rowHeight = 45+state[1]+"%"; //37
        this.uiService.columnCountNumber = 4;
        this.gutterSize = "24";
      }
      if (state[0].breakpoints[Breakpoints.XLarge] && !state[0].breakpoints[Breakpoints.Large]) {
        //this.rowHeight = "45%";
        this.rowHeight = 45+state[1]+"%";
        this.uiService.columnCountNumber = 5;
        this.gutterSize = "24";
      }
      if (state[0].breakpoints[Breakpoints.Web]) {
        this.rowHeight = 45+state[1]+"%";
        this.uiService.columnCountNumber = 5;
        this.gutterSize = "24";
      }
    }
  }

  /**
   * Sets the filter value so the API can update the data on the page.
   * @param event Event containing user input from the filter
   */
  applyFilter(event: Event) {
    this.filterValue = (event.target as HTMLInputElement).value.trim().toLowerCase();
    this.uiService.filterString = this.filterValue;
  }

  openNewTypeDialog() {
    const dialogRef = this.dialog.open(NewTypeDialogComponent, {
      minHeight: "70vh",
      minWidth:"80vw"
    });
    dialogRef.afterClosed().subscribe((result) => {
      this.mapTo(result);
    })
  }

  mapTo(results: Array<{ name: string, value: string | boolean }>) {
    let resultingObj: Partial<PlatformType> = {};
    results.forEach((el) => {
      let name = el.name.charAt(0).toLowerCase() + el.name.slice(1);
      (resultingObj as any)[name]=el.value
    })
    this.typesService.createType(resultingObj).subscribe();
  }

  getWidthString() {
    return 100 / this.columnCount.getValue()+"% -"+this.gutterSize+'px';
  }
  getMarginString() {
    return this.gutterSize+'px'
  }

  openSettingsDialog() {
    combineLatest([this.typesService.inEditMode, this.uiService.BranchId]).pipe(
      take(1),
      switchMap(([edit, id]) => this.dialog.open(ColumnPreferencesDialogComponent, {
        data: {
          branchId: id,
          allHeaders2: [],
          allowedHeaders2: [],
          allHeaders1: [],
          allowedHeaders1: [],
          editable: edit,
          headers1Label: '',
          headers2Label: '',
          headersTableActive: false,
        }
      }).afterClosed().pipe(
        take(1),
        switchMap((result) => this.typesService.updatePreferences(result))))
    ).subscribe();
  }
}
