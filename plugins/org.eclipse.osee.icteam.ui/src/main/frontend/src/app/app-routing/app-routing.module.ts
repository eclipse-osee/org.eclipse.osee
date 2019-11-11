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
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AppComponent } from '../app.component';
import { LoginComponent } from '../login/login.component';
import { DashboardComponent } from '../dashboard/dashboard.component';
import { DisplayProjectComponent } from '../project/displayproject/displayproject.component';
import { TeamComponent } from '../team/team.component';
import { UserDashboardComponent } from '../dashboard/user-dashboard/user-dashboard.component';
import { TaskComponent } from '../workitem/task/task.component';
import { ReleaseViewComponent } from '../project/displayproject/release-view/release-view.component';
import { SprintViewComponent } from '../project/displayproject/sprint-view/sprint-view.component';
import { AuthGuard } from '../guards/auth/auth.guard';

const appRouters: Routes = [

  { path: 'login', component: LoginComponent },
  {
    path: 'dashboard',
    component: DashboardComponent, canActivate: [AuthGuard],
    children: [
      {
        path: 'projectDashboard/:projecId',
        component: DisplayProjectComponent,
        canActivate: [AuthGuard],
        children: [
          {
            path: '',
            redirectTo: 'Releases',
            pathMatch : 'full',
            canActivate: [AuthGuard]
          },
          {
            path: 'Releases',
            component: ReleaseViewComponent,
            canActivate: [AuthGuard],
            children: [
              {
                path: 'Sprint/:sprintId',
                component: SprintViewComponent,
                canActivate: [AuthGuard]
              },
              {
                path: 'Backlog',
                component: SprintViewComponent,
                canActivate: [AuthGuard]
              }
            ]
          }
        ]
      },
      {
        path: 'userDashboard',
        component: UserDashboardComponent,
        canActivate: [AuthGuard]
      },
      { path: ':projectId/:id', component: TaskComponent, canActivate: [AuthGuard] },

    ]
  },
  { path: '**', redirectTo: 'login' }

];


@NgModule({
  providers: [AuthGuard],
  imports: [RouterModule.forRoot(appRouters, { enableTracing: true, useHash: true , onSameUrlNavigation: 'reload'} )],
  exports: [RouterModule]
})
export class AppRoutingModule { }
