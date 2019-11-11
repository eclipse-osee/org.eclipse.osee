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
import { Component, OnInit, ViewEncapsulation, OnDestroy } from '@angular/core';
import { DragulaService } from 'ng2-dragula';
import { DashboardService } from '../service/dashboard.service';
import { UsersService } from '../service/users.service';
import { ProjectModel } from '../model/projectModel';
import { NgbModal, ModalDismissReasons, NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { TeamService } from '../service/team.service';

@Component({
  selector: 'app-team',
  templateUrl: './team.component.html',
  encapsulation: ViewEncapsulation.None,
  styleUrls: ['./team.component.css']
})
export class TeamComponent implements OnInit, OnDestroy {

  ngOnDestroy(): void {
    this.dragulaService.destroy('bag-one');
  }

  projectList: any;
  usersList: any;
  teamData: ProjectModel;
  teamMembers: Array<String>;
  teamLeads: Array<String>;
  teamSavingResult: String;
  isAddUserWindowActive: Boolean;
  ldapSearchUsers: any;
  userGuid: String;
  selectedProject: any;
  projectsDisable: Boolean;
  userDetails: any;
  teamMemberList: any;
  teamLeadList: any;
  teamTittle: any;
  teamDetailsForUpdate: any;
  searchUser: any;
  constructor(private dragulaService: DragulaService, private dashboardService: DashboardService,
    private userService: UsersService, public activeModal: NgbActiveModal, private teamService: TeamService) {

    this.teamMembers = new Array<String>();
    this.teamLeads = new Array<String>();

    dragulaService.drop.subscribe((value) => {
      this.onDrop(value);
    });
    dragulaService.remove.subscribe((value) => {
      this.onRemove(value);
    });

    this.isAddUserWindowActive = false;
    this.teamData = new ProjectModel();
    this.teamData.attributeMap = new Map<String, any>();
    this.teamData.attributeMap['TeamMembers'] = new Array<String>();
    this.teamData.attributeMap['TeamLeads'] = new Array<String>();
    let self = this;
    this.dragulaService.setOptions('bag-one', {
      removeOnSpill: true,
      copy: function (el, source) {
        if (source.id === 'users') {
          return true;
        } else {
          return false;
        }
      },
      copySortSource: false,
      accepts: function (el, target, source, sibling) {
        let tempData = el.getAttribute('guiddata')
        if (target.id == 'members') {
          if (self.teamMembers.indexOf(tempData) == -1) {
            return true;
          }
          return false;
        } else if (target.id == 'leads') {
          if (self.teamLeads.indexOf(tempData) == -1) {
            return true;
          }
          return false;
        } else {
          return false;
        }
      }
    });

  }




  ngOnInit() {
    this.userDetails = JSON.parse(sessionStorage.getItem('userDetails'));
    this.userGuid = JSON.parse(sessionStorage.getItem('userDetails')).userGuid;


    this.userService.getAllUsers().subscribe(
      (result: Response) => {
        console.log(result);
        this.usersList = result['artifactList'];
      },
      (error) => {
        console.error(error);
      }
    );

    if (this.selectedProject) {
      this.projectList = new Array(this.selectedProject);
      this.projectsDisable = true;
      console.log("this.selectedProject", this.selectedProject);
      this.teamService.get(this.selectedProject.guid).subscribe(
        (result: Response) => {
          console.log(result);
          this.teamDetailsForUpdate = result['artifactList'][0];
          this.teamLeadList = result['artifactList'][0].relationMap.TeamLead;
          console.log("this.teamLeadList", this.teamLeadList);
          this.teamLeadList.forEach(teamLead => {
            this.teamLeads.push(teamLead.guid);
          });
          this.teamMemberList = result['artifactList'][0].relationMap.TeamMember;
          this.teamMemberList.forEach(teamMemeber => {
            this.teamMembers.push(teamMemeber.guid);
          });
          console.log("this.teamLeadList", this.teamMemberList);
          this.teamTittle = result['artifactList'][0].name;
          // this.projectList = result['artifactList'];
        },
        (error) => {
          console.error(error);
        }
      );

    } else {
      this.dashboardService.getUserSpecificProjects(this.userGuid).subscribe(
        (result: Response) => {
          console.log(result);
          this.projectList = result['artifactList'];
        },
        (error) => {
          console.error(error);
        }
      );

    }



  }


  /**
 * value[0] -- dropped setting id
 * value[1] -- dropped element 
 * value[2] -- target container element
 * value[3] -- source container element
 */
  onDrop(value: any) {
    if (value[2] != null) {
      let tempData = value[1].getAttribute('guiddata')
      if (value[3].id == 'members') {
        if (this.teamMembers.indexOf(tempData) != -1) {
          this.teamMembers = this.teamMembers.filter(e => e !== tempData);
        }
      }
      if (value[3].id == 'leads') {
        if (this.teamLeads.indexOf(tempData) != -1) {
          this.teamLeads = this.teamLeads.filter(e => e !== tempData);
        }
      }
      if (value[2].id == 'members') {
        if (this.teamMembers.indexOf(tempData) == -1) {
          this.teamMembers.push(tempData)
        }
      } else if (value[2].id == 'leads') {
        if (this.teamLeads.indexOf(tempData) == -1) {
          this.teamLeads.push(tempData)
        }
      }
      console.log(value[2].id);
    }
  }

  onRemove(value: any) {
    let tempData = value[1].getAttribute('guiddata')
    if (value[3].id == 'members') {
      if (this.teamMembers.indexOf(tempData) != -1) {
        this.teamMembers = this.teamMembers.filter(e => e !== tempData);
      }
    } else if (value[3].id == 'leads') {
      if (this.teamLeads.indexOf(tempData) != -1) {
        this.teamLeads = this.teamLeads.filter(e => e !== tempData);
      }
    }
  }


  save(teamTittle: String, projectGuid: any) {
    if (teamTittle == undefined || teamTittle == "" || teamTittle.length == 0) {
      this.teamSavingResult = "Please Enter team tittle";
      return;
    }
    else if (this.teamMembers.length == 0 || this.teamLeads.length == 0) {
      this.teamSavingResult = "Please select the team member and team leads";
      return;
    }

    const teamData = new ProjectModel();
    teamData.currentLoggedInUser = this.userDetails.username;
    teamData.currentUserId = this.userDetails.username;
    teamData.name = teamTittle;
    teamData.attributeMap = new Map();
    teamData.attributeMap['TeamLeads'] = this.teamLeads;
    teamData.attributeMap['TeamMembers'] = this.teamMembers;
    teamData.attributeMap['ats.Workflow Definition'] = new Array("WorkDef_ICTeam");
    teamData.attributeMap['Project'] = new Array(projectGuid);
    if (this.teamDetailsForUpdate && this.teamDetailsForUpdate.guid) {
      teamData.guid = this.teamDetailsForUpdate.guid;
      this.teamService.update(teamData).subscribe(
        result => {
          console.log(result);
          this.activeModal.close();
        }, error => {
        }
      );
    } else {
      this.teamService.create(teamData).subscribe(
        result => {
          console.log(result);
          this.activeModal.close();
        }, error => {
          this.activeModal.close;
        }
      );
    }

    console.log(teamData);
  }

  addUserWindow() {
    this.isAddUserWindowActive = true;
    console.log("addUserWindow");

  }
  searchUserFormLDAp(userData: String) {
    console.log("searchUserFormLDAp");
    this.userService.searchUserFromLdap(userData).subscribe(
      (result) => {
        console.log(result);

        this.ldapSearchUsers = result['artifactList'];
      },
      error => {
        console.error('Error while geting LDAP user');

      }

    );

  }

  addUserToDB(ldapUser: any) {
    let flag: any;
    flag = false;
    this.usersList.forEach(element => {
      if (element.attributeMap['User Id'][0] === ldapUser.attributeMap['User Id'][0]) {
        this.teamSavingResult = 'User Already Present';
        flag = true;
      }
    });

    if (!flag) {
      let ldapuserData = new ProjectModel();
      ldapuserData = Object.assign({}, ldapUser);
      ldapuserData.currentLoggedInUser = this.userDetails.username;
      ldapuserData.attributeMap = new Map<any, Array<any>>();
      ldapuserData.name = ldapUser.attributeMap['Name'][0];
      ldapuserData.attributeMap["1152921504606847082"] = ldapUser.attributeMap['Email'];
      ldapuserData.attributeMap["1152921504606847088"] = ldapUser.attributeMap['Name'];
      ldapuserData.attributeMap["1152921504606847073"] = ldapUser.attributeMap['User Id'];
      console.log(ldapuserData);

      this.userService.saveLdapUser(ldapuserData).subscribe(
        result => {
          this.usersList.push(result['artifactList'][0]);
          //this.isAddUserWindowActive = false;
        },
        error => {
          console.log("Error while saving user", error);

        }
      );
    }

  }

}
