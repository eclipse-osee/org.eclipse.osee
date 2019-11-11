/*********************************************************************
 * Copyright (c) 2020 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, ActivatedRoute, Params, RouterOutlet, NavigationEnd} from '@angular/router';
import { NgbModalOptions, NgbPopoverConfig  } from '@ng-bootstrap/ng-bootstrap';
import { DataserviceService } from '../../../service/dataservice.service';
import { ProjectService } from '../../../service/project.service';
import { ProjectModel } from '../../../model/projectModel';

@Component({
  selector: 'app-release-view',
  templateUrl: './release-view.component.html',
  styleUrls: ['./release-view.component.css'],
  providers: [NgbPopoverConfig],
})
export class ReleaseViewComponent implements OnInit, OnDestroy {

  selectedSprint: any;
  milstoneList: any;
  selectedProjectGuid: any;
  sprintId: any;

  highlightSprintId: any;
  subscription: any;


  rowData: any;
  p = 1;
  modalOption: NgbModalOptions = {};
  constructor(config: NgbPopoverConfig, private route: ActivatedRoute,
     private router: Router,
     private dataService: DataserviceService, private outlet: RouterOutlet , private projectService :ProjectService ) {
      config.placement = 'right';
      config.triggers = 'hover';
  }

  public ngOnInit() {
    this.modalOption.backdrop = 'static';
    this.modalOption.keyboard = false;
    const urlData = this.router.url.split('/');
    const rege = /%2(52)*B/g;
    if (urlData[urlData.length - 2] === 'Sprint') {
      const sprintGuid: String = urlData[urlData.length - 1];
      const guid = sprintGuid.replace(rege, '+');
      this.highlightSprintId = guid;
    }
 
    this.router.routeReuseStrategy.shouldReuseRoute = function () {
      return false;
      };

  }

  public ngOnDestroy() {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }

  
  onNodeSelected(selectedObject: any) {
    this.sprintId = selectedObject.node.id;
    if (this.sprintId && this.sprintId !== 'release') {
      this.highlightSprintId = selectedObject.node.id;
      this.router.navigate( ['Sprint', this.sprintId], { relativeTo: this.outlet.activatedRoute } );

    }
  }
}

