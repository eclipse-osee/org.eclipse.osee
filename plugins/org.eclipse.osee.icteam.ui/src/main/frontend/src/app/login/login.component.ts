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
import { Component, OnInit } from "@angular/core";
import { NgForm } from "@angular/forms";
import { AuthService } from "../service/auth.service";
import { Router, ActivatedRoute } from "@angular/router";
import { environment } from "../../environments/environment";

@Component({
  selector: "app-login",
  templateUrl: "./login.component.html",
  styleUrls: ["./login.component.scss"],
})
export class LoginComponent implements OnInit {
  domains = [];
  errorMessage: String;
  tittle: string;
  logo: string;
  return: string = "";

  constructor(
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    console.log("this is login component");
  }

  ngOnInit() {
    this.tittle = environment.title;
    this.logo = environment.logo;
    this.domains = new Array("us", "apac", "de");
    console.log(this.route.queryParams);

    this.route.queryParams.subscribe(
      (params) => (this.return = params["return"])
    );
  }

  onSubmit(loginData: NgForm) {
    console.log(loginData.form.value);
    this.authService.login(loginData.form.value).subscribe(
      (response: Response) => {
        console.log(response);

        if (loginData.form.value.userId === "user") {
          const userDetails = { userGuid: "Axz1FpRLAjPe9RwA1IwA" };
          // userDetails.set("userGuid", 'AAHWhM685UrjBVk6V_AA');
          sessionStorage.setItem("userDetails", JSON.stringify(userDetails));
        } else {
          sessionStorage.setItem("userDetails", JSON.stringify(response));
        }
        if (this.return) {
          this.router.navigateByUrl(this.return);
        } else {
          this.router.navigateByUrl("/dashboard/userDashboard");
        }
      },
      (error) => {
        this.errorMessage = "Please check username/Password";
        console.log("Error while login", error);
        setTimeout(function () {
          this.errorMessage = "undefined";
        }, 1000);
      }
    );
    // this.router.navigate(['/login']);
  }
}
