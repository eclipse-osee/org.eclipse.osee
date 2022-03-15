import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { Observable, of } from 'rxjs';
import {startWith, map} from 'rxjs/operators';

export interface CommandGroup {
  groupName: string;
  commands: string[];
}
export const _filter = (opt: string[], value: string): string[] => {
  const filterValue = value.toLowerCase();
  return opt.filter(item => item.toLowerCase().indexOf(filterValue) === 0);
};

@Component({
  selector: 'app-grid-commander',
  templateUrl: './grid-commander.component.html',
  styleUrls: ['./grid-commander.component.sass']
})
export class GridCommanderComponent implements OnInit {
  columnDefs = [
    { field: "make", sortable:true, filter: true, checkboxSelection: true },
    { field: "model", sortable:true, filter: true },
    { field: "price", sortable:true, filter: true }
  ];

  rowData : any;

commandForm: FormGroup = this._formBuilder.group({
  commandGroup: '',
});

commandGroups: CommandGroup[] = [{
  groupName: 'Add new',
  commands: ['add row', 'add table']
}, {
  groupName: 'Filter table',
  commands: ['filter']
}, {
  groupName: 'Hide object',
  commands: ['hide']
}, {
  groupName: 'Open object in new tab',
  commands: ['open ']
}, {
  groupName: 'Remove selected rows',
  commands: ['remove']
}, {
  groupName: 'Show object in current tab',
  commands: ['show']
}, {
  groupName: 'Sort',
  commands: ['sort']
}];

commandGroupsOptions: Observable<CommandGroup[]> = of(this.commandGroups);

  constructor(private http: HttpClient, private _formBuilder: FormBuilder) {

  }

  ngOnInit() {
    this.rowData = this.http.get("https://www.ag-grid.com/example-assets/small-row-data.json");

    this.commandGroupsOptions = this.commandForm.get('commandGroup')!.valueChanges
      .pipe(
        startWith(''),
        map(value => this._filterGroup(value))
      );
  }

  private _filterGroup(value: string): CommandGroup[] {
    if (value) {
      return this.commandGroups
        .map(group => ({groupName: group.groupName, commands: _filter(group.commands, value)}))
        .filter(group => group.commands.length > 0);
    }
    return this.commandGroups;
  }
}
