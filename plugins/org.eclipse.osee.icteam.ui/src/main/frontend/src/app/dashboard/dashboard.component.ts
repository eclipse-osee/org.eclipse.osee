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
import { DashboardService } from '../service/dashboard.service';
import { DataserviceService } from '../service/dataservice.service';
import { Subscription ,  Observable ,  Observer } from 'rxjs';
import { Router, ActivatedRoute } from '@angular/router';
import { environment } from '../../environments/environment';
import { timeout } from 'q';


@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})

export class DashboardComponent implements OnInit, OnDestroy {

  projectList = [];
  selectedProject: any;
  selectedProjectObservable: Observable<string>;
  private observer: Observer<string>;
  userGuid: String;
  userFullName: String;
  private subscription: Subscription;
  //projectObservable;


  constructor(private dashboardService: DashboardService, private data: DataserviceService,
    private router: Router, private route: ActivatedRoute) {
    console.log("this is dashboard component");
    this.selectedProjectObservable = new Observable<string>(observer =>
      this.observer = observer
    );
  }

  public ngOnInit() {
    let userDetails = JSON.parse(sessionStorage.getItem('userDetails'));
    this.userGuid = userDetails.userGuid;
    this.userFullName = userDetails.userFullName;
   this.subscription = this.data.projectList.subscribe(data => {
      if (data.length > 1) {
        this.projectList = data;
      }
      else {
        this.projectList.push(data[0]);
    }
    })

    this.projectList = this.data.ProjectData;
    // Observable.
    // setTimeout(() => {
      if (this.projectList.length <= 0) {
        this.dashboardService.getUserSpecificProjects(this.userGuid).subscribe(
          (result: Response) => {
            this.data.ProjectData.splice(0, this.data.ProjectData.length);
            result['artifactList'].forEach(element => {
              this.data.ProjectData.push(element);
            });
          },
          (error) => {
            console.error(error);

          }
        );
      }
    // }, 5000);

  }

  showSelectedProject(selectedProject) {
    console.log(JSON.stringify(selectedProject));
    console.log("Inside selected " + selectedProject.name);
    console.log("Guid " + selectedProject.guid);
    console.log("Guid " + selectedProject.attributes.Shortname);
    this.selectedProject = selectedProject;
    console.log("After setting " + this.selectedProject);

    if (this.observer !== undefined)
      this.observer.next(this.selectedProject);
    console.log("after observer");
    this.router.navigate(['project'], { relativeTo: this.route });
  }
  ngOnDestroy() {
    this.subscription.unsubscribe();
  }


}
