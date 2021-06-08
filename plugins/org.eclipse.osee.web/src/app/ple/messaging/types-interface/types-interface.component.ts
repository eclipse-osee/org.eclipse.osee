import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PlMessagingTypesUIService } from './services/pl-messaging-types-ui.service';

@Component({
  selector: 'app-types-interface',
  templateUrl: './types-interface.component.html',
  styleUrls: ['./types-interface.component.sass']
})
export class TypesInterfaceComponent implements OnInit {
  filterValue: string = "";
  constructor(private route: ActivatedRoute, private uiService: PlMessagingTypesUIService) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe((values) => {
      this.filterValue = values.get('type')?.trim().toLowerCase() || '';
      this.uiService.BranchIdString = values.get('branchId') || '';
    })
  }

}
