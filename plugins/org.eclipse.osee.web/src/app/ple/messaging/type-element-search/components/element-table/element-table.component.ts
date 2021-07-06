import { Component, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { CurrentElementSearchService } from '../../services/current-element-search.service';
import { element } from '../../types/element';

@Component({
  selector: 'osee-typesearch-element-table',
  templateUrl: './element-table.component.html',
  styleUrls: ['./element-table.component.sass']
})
export class ElementTableComponent implements OnInit {
  dataSource = new MatTableDataSource<element>();
  headers = [
    'name',
    'platformTypeName2',
    'interfaceElementAlterable',
    'description',
    'notes'];
  constructor (private elementService: CurrentElementSearchService) {
    this.elementService.elements.subscribe((val) => {
      this.dataSource.data = val;
    })
  }

  ngOnInit(): void {
  }

  valueTracker(index: any, item: any) {
    return index;
  }
}
