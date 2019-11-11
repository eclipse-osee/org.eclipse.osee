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
import { Component, OnInit, Input, ViewChildren } from '@angular/core';
import { Router, ActivatedRoute, RouterOutlet, Params, NavigationEnd } from '@angular/router';
import { DashboardComponent } from '../../dashboard/dashboard.component';
import { TeamComponent } from '../../team/team.component';
import { PackageComponent } from '../../package/package.component';
import { ReleaseComponent } from '../../release/release.component';
import { WorkitemComponent } from '../../workitem/workitem.component';
import { NgbModal, NgbActiveModal, NgbModalOptions } from '@ng-bootstrap/ng-bootstrap';
import { ProjectService } from '../../service/project.service';
import { WorkitemService } from '../../service/workitem.service';
import { DataserviceService } from '../../service/dataservice.service';
import { Subscription, ReplaySubject } from 'rxjs';

@Component({
    selector: 'app-project',
    templateUrl: './displayproject.component.html',
    styleUrls: ['./displayproject.component.scss']
})
export class DisplayProjectComponent implements OnInit {
    showNav: Boolean;
    userDetails: any;
    selectedProjectGuid: any;
    modalOption: NgbModalOptions = {};
    selectedProject: any;
    selectedUsers: any;
    usersList: any;
    columnDefs: any;
    dropDownNav: any;
    selectedRelease: any;
    releaseList = [];
    closereleaseList = [];
    private subscription: Subscription;

    constructor(private router: Router,private outlet: RouterOutlet, private route: ActivatedRoute, private dashBoard: DashboardComponent, private modalService: NgbModal, private projectService: ProjectService, private workitemService: WorkitemService,
        private data: DataserviceService) {
        this.router.events.subscribe((res) => {
            console.log(this.router.url, 'Current URL');
            const udata = this.router.url.split('/');
            let presentRoute = udata[udata.length - 1];
            if (presentRoute === 'Releases') {
                this.dropDownNav = 'Releases';
            }  else if (presentRoute === 'Backlog') {
              this.dropDownNav = 'Releases';
            } else {
              presentRoute = udata[udata.length - 3];
              if (presentRoute === 'Releases') {
                this.dropDownNav = 'Releases';
            }
            }
        });

    }

    public ngOnInit() {
        this.modalOption.backdrop = 'static';
        this.modalOption.keyboard = false;
        this.showNav = false;
        this.userDetails = JSON.parse(sessionStorage.getItem('userDetails'));
        this.route.params.subscribe((projecId: Params) => {
            this.selectedProjectGuid = projecId.projecId;
            const guid: string = this.selectedProjectGuid;
            const rege = /%2(52)*B/g;
             this.renderProjects(guid.replace(rege, '+'));
        });
    }

    renderProjects(selectedProjectGuid: any): void {
      console.log(selectedProjectGuid);
        this.projectService.getProjectByUuid(selectedProjectGuid).subscribe(
            (result: Response) => {
                if (result) {
                    this.selectedProject = result['artifactList'][0];
                }
            },
            (error) => {
                console.error(error);
            }
        );
        this.projectService.getCloseReleaseForProject(selectedProjectGuid).subscribe(
          (res: Response) => {
            this.closereleaseList = res['list'];
          },
          (error) => {
            console.error(error);
          }
        );
        this.projectService.getOpenReleaseForProject(selectedProjectGuid).subscribe(
          (res: Response) => {
            this.releaseList = res['list'];
          },
          (error) => {
            console.error(error);
          }
        );
    }






    closeNav(){
        this.showNav = false
    }

    openNav() {
        this.showNav = this.showNav === true ? false : true;
    }


    createTeam(): void {
        const modalOption = Object.assign({}, this.modalOption);
        modalOption.size = 'lg';
        modalOption.windowClass = 'icteam-xlsize';
        const activeModal = this.modalService.open(TeamComponent, modalOption);
        activeModal.componentInstance.selectedProject = this.selectedProject;
    }

    createPackage(): void {
        const modalOption = Object.assign({}, this.modalOption);
        modalOption.size = 'lg';
        const activeModal = this.modalService.open(PackageComponent, modalOption);
        console.log('this.selectedProject', this.selectedProject);
        activeModal.componentInstance.selectedProject = this.selectedProject;
    }


    createTask(): void {
        const activeModal = this.modalService.open(WorkitemComponent, this.modalOption);
        activeModal.componentInstance.selectedProject = this.selectedProject;
        activeModal.result.then((result) => {
            if (result === 'newTaskCrated') {
                this.data.taskMessageSource.next('taskCreated');
                this.router.navigated = false;
                this.router.navigate([this.router.url]);
            }
        }, (reason) => {
        });
    }


    createRelease(): void {
        console.log('createRelease');
        const modalOption = Object.assign({}, this.modalOption);
        modalOption.size = 'lg';
        const activeModal = this.modalService.open(ReleaseComponent, modalOption);
        activeModal.componentInstance.selectedProject = this.selectedProject;
        activeModal.result.then((result) => {
            console.log('${result}', result);
            if (result.length > 0) {
               
                this.data.sendRelease(result);
            }
           
            const url = this.router.url;
            this.router.navigate([url]);
        }, (reason) => {
            
        });
    }


    
    onUserSelect(item: any) {
        console.log(item);
        console.log(item.attributeMap['ats.Actionable Item']);
        console.log(this.selectedUsers);
        console.log(this.selectedUsers.map(option => this.usersList.find(o => o.id === option.id)));
    }

    dropdownNav(item: any) {
        this.router.navigateByUrl(item);
    }


    outSideClick(type: Boolean) {
        console.log('type', type);
        type = false;

    }

    getSelectedSprint() {
      console.log(this.selectedRelease);
      console.log(this.router.url);
      if (this.selectedRelease === 'Backlog') {
        this.router.navigate( ['Releases/Backlog'], { relativeTo: this.outlet.activatedRoute } );
      } else {
        this.router.navigate( ['Releases/Sprint', this.selectedRelease.guid], { relativeTo: this.outlet.activatedRoute } );
      }
    }


}
