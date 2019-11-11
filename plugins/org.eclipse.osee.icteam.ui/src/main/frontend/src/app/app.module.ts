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
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule, } from '@angular/forms';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { AppRoutingModule } from './app-routing/app-routing.module';
import { DashboardComponent } from './dashboard/dashboard.component';
import { AuthService } from './service/auth.service';
import { HttpInterceptorService } from './interceptor/http-interceptor.service';

import { DashboardService } from './service/dashboard.service';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { DisplayProjectComponent } from './project/displayproject/displayproject.component';
import { ProjectComponent } from './project/project.component';

import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ProjectService } from './service/project.service';
import { DataserviceService } from './service/dataservice.service';
import { DragulaModule } from 'ng2-dragula';
import { TeamComponent } from './team/team.component';
import { UsersService } from './service/users.service';
import { NameFilterPipe } from './pipes/namefilter';
import { PackageComponent } from './package/package.component';
import { PackageService } from './service/package.service';
import { TeamService } from './service/team.service';
import { WorkitemComponent } from './workitem/workitem.component';
import { WorkitemService } from './service/workitem.service';
import { AngularMultiSelectModule } from 'angular2-multiselect-dropdown/angular2-multiselect-dropdown';
import { ReleaseComponent } from './release/release.component';
import { LoaderComponent } from './loader/loader.component';
import { LoaderService } from './service/loader.service';
import { UserDashboardComponent } from './dashboard/user-dashboard/user-dashboard.component';
import { ChartsModule } from 'ng2-charts';
import { TaskViewModalComponent } from './workitem/task-view-modal/task-view-modal.component';
import { DroplistComponent } from './utils/droplist/droplist.component';
import { TaskComponent } from './workitem/task/task.component';
import { MultiSelectSearchFilter } from './utils/dropdown/search-filter.pipe';
import { MultiselectDropdownComponent } from './utils/dropdown/dropdown.component';
import { AutofocusDirective } from './utils/dropdown/autofocus.directive';
import { SplitPipe } from './pipes/split.pipe';
import { NgbdModalContent } from './dashboard/user-dashboard/user-dashboard.component';
import { FileUploadModule } from 'ng2-file-upload';
import { CookieService } from 'ngx-cookie-service';
import { SpinnerComponent } from './spinner/spinner.component';
// import { DataTablePaginationComponent } from './utils/pagination/pagination.component';
import { ClickOutsideModule } from 'ng4-click-outside';
import { FilterPipe } from './pipes/filter';
import { NgxPaginationModule } from 'ngx-pagination';
import { IcTableComponent } from './utils/ic-table/ic-table.component';
import { TableTrComponent } from './utils/ic-table/table-tr/table-tr.component';
import { TableTrDirective } from './utils/ic-table/table-tr.directive';
import { BiNavigationComponent } from './dashboard/bi-navigation/bi-navigation.component';
import { ReleaseViewComponent } from './project/displayproject/release-view/release-view.component';


import { LinkTaskComponent } from './workitem/task/link-task/link-task.component';
import { SprintViewComponent } from './project/displayproject/sprint-view/sprint-view.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import { UserDashboardService } from './service/userdashborad.service';
import {MatIconModule } from '@angular/material/icon';
import {MatRadioModule } from '@angular/material/radio';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatSortModule, MatSortable} from '@angular/material/sort';
import {MatTableModule} from '@angular/material/table';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatSelectModule} from '@angular/material/select';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';



@NgModule({
  declarations: [
    LoaderComponent,
    AppComponent,
    LoginComponent,
    DashboardComponent,
    DisplayProjectComponent,
    ProjectComponent,
    TeamComponent,
    NameFilterPipe,
    MultiSelectSearchFilter,
    PackageComponent,
    WorkitemComponent,
    ReleaseComponent,
    // LoaderComponent,
    UserDashboardComponent,
    TaskViewModalComponent,
    DroplistComponent,
    TaskComponent,
    SplitPipe,
    NgbdModalContent,
    MultiselectDropdownComponent,
    AutofocusDirective,
    SpinnerComponent,
    FilterPipe,
    IcTableComponent,
    TableTrComponent,
    TableTrDirective,
    BiNavigationComponent,
    ReleaseViewComponent,
    LinkTaskComponent,
    SprintViewComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    NgbModule.forRoot(),
    DragulaModule,
    AngularMultiSelectModule,
    ChartsModule,
    FileUploadModule,
    ClickOutsideModule,
    NgxPaginationModule,
    // PerfectScrollbarModule,
    // TreeModule,
    MatIconModule,
    MatRadioModule,
    MatPaginatorModule,
    MatSortModule,
    MatTableModule,
    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
    MatButtonModule
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpInterceptorService,
      multi: true
    },
    AuthService,
    DashboardService,
    ProjectService,
    DataserviceService,
    UsersService,
    TeamService,
    PackageService,
    WorkitemService,
    LoaderService,
    CookieService,
    UserDashboardService,
  ],
  bootstrap: [AppComponent],
  entryComponents: [
    ProjectComponent,
    TeamComponent,
    PackageComponent,
    ReleaseComponent,
    WorkitemComponent,
    TaskViewModalComponent,
    NgbdModalContent,
    LinkTaskComponent
  ]

})
export class AppModule { }
