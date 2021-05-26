import { Component, Inject,  OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { user } from '../../types/user-data-user';
import { UserDataAccountService } from '../../services/user-data-account.service';
import { UserDataCurrentUserService } from '../../services/user-data-current-user.service';
import { UserDataUIStateService } from '../../services/user-data-uistate.service';

@Component({
  selector: 'osee-display-user',
  templateUrl: './display-user.component.html',
  styleUrls: ['./display-user.component.sass']
})
export class DisplayUserComponent implements OnInit {
  userInfo: Observable<user> = this.accountService.getUser();
  
  constructor(private accountService: UserDataAccountService) { }

  ngOnInit(): void {
  }
}