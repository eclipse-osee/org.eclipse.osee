/*********************************************************************
* Copyright (c) 2023 Boeing
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     Boeing - initial API and implementation
**********************************************************************/

function searchButton() {
	$ids = document.getElementById('searchId').value;
	if ($ids == null || $ids == "") {
		alert("Must enter search ids.");
	}else {
	   $url = "/ats/ui/action/" + $ids;
	   window.open($url);
    }
}

var button = document.getElementById("searchButton");
button.addEventListener("click", searchButton);

function checkForReturn(e) {
    var key = e.keyCode;
    if (key == 13) { // 13 is enter
      searchButton();
    }
};
