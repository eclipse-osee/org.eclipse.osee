import { Component, OnInit } from '@angular/core';
import { SearchService } from '../../services/router/search.service';

@Component({
  selector: 'osee-typesearch-element-table-search',
  templateUrl: './element-table-search.component.html',
  styleUrls: ['./element-table-search.component.sass']
})
export class ElementTableSearchComponent implements OnInit {
  searchTerm: string = "";
  constructor (private searchService: SearchService) {
    this.searchService.searchTerm.subscribe((val) => {
      this.searchTerm = val;
    })
   }

  ngOnInit(): void {
  }

  applyFilter(event: Event) {
    this.searchService.search=(event.target as HTMLInputElement).value.trim().toLowerCase();
  }

}
