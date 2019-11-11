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
import { Component, OnInit, Input } from '@angular/core';
import { TeamComponent } from '../../team/team.component';
import { PackageComponent } from '../../package/package.component';
import { ReleaseComponent } from '../../release/release.component';
import { WorkitemComponent } from '../../workitem/workitem.component';
import { ProjectComponent } from '../../project/project.component';
import { NgbModal, NgbActiveModal, NgbModalOptions } from '@ng-bootstrap/ng-bootstrap';
import { AuthService } from '../../service/auth.service';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';


@Component({
  selector: 'app-bi-navigation',
  templateUrl: './bi-navigation.component.html',
  styleUrls: ['./bi-navigation.component.scss']
})
export class BiNavigationComponent implements OnInit {
  title: string;

  @Input() projectList;
  userFullName: any;
  config: any;
  modalOption: NgbModalOptions = {};
  SearchProject: any;

  constructor(private modalService: NgbModal, private authService: AuthService, private router: Router) { }

  ngOnInit() {
    this.modalOption.backdrop = 'static';
    this.modalOption.keyboard = false;
    this.title = environment.title;
    const userDetails = JSON.parse(sessionStorage.getItem('userDetails'));
    this.userFullName = userDetails.userFullName;
  }


  createProject(): void {
    this.modalService.open(ProjectComponent, this.modalOption);
  }

  createTeam(): void {
    this.modalService.open(TeamComponent, { windowClass: 'icteam-xlsize', size: 'lg' });
  }

  createPackage(): void {
    this.modalService.open(PackageComponent);
  }


  createTask(): void {
    this.modalService.open(WorkitemComponent);
  }


  createRelease(): void {
    console.log('createRelease');

    this.modalService.open(ReleaseComponent);
  }

  onLogout(): void {
    console.log('Logout action');
    this.authService.logout().subscribe(
      (response: Response) => {
        this.router.navigate(['/login']);
      },
      () => {
        console.log('Error while logout')
      }
    );

  }



}
