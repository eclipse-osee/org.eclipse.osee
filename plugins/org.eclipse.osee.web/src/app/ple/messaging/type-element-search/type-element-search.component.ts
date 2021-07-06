import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Params } from '@angular/router';
import { map } from 'rxjs/operators';
import { RouterStateService } from './services/router/router-state.service';

@Component({
  selector: 'osee-typesearch-type-element-search',
  templateUrl: './type-element-search.component.html',
  styleUrls: ['./type-element-search.component.sass']
})
export class TypeElementSearchComponent implements OnInit {

  constructor(private route: ActivatedRoute, private routerState: RouterStateService) { }

  ngOnInit(): void {
    this.route.params.pipe(
      map((params: Params) => { if (Number(params['branchId']) > 0) { this.routerState.id = params['branchId'] }; if (params['branchType'] === 'product line' || params['branchType'] === 'working') { this.routerState.type = params['branchType'] }})
    ).subscribe();
  }

}
