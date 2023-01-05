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
	function post(url, stateChangeHandler) {
		httpRequest("POST", url, stateChangeHandler);
	}
	
	function post_with_parms(url, params, method) {
		method = method || "post"; // Set method to post by default if not specified.

		// The rest of this code assumes you are not using a library.
		// It can be made less wordy if you use one.
		var form = document.createElement("form");
		form.setAttribute("method", method);
		form.setAttribute("action", url);

		for(var key in params) {
			if(params.hasOwnProperty(key)) {
				var hiddenField = document.createElement("input");
				hiddenField.setAttribute("type", "hidden");
				hiddenField.setAttribute("name", key);
				hiddenField.setAttribute("value", params[key]);
				console.log("key [" + key + "] value [" + params[key] + "]");

				form.appendChild(hiddenField);
			}
		}

		document.body.appendChild(form);
		form.submit();
   }
	
	function get(url, stateChangeHandler) {
		httpRequest("GET", url, stateChangeHandler);
	}
	
	function httpRequest(httpVerb, url, stateChangeHandler) {
		var httpRequest = new XMLHttpRequest()
		httpRequest.prevDataLength = 0;
		httpRequest.onreadystatechange = stateChangeHandler;
		httpRequest.open(httpVerb, url);
		httpRequest.send();
	}

	function removeChildren(node) {	
		while (node.hasChildNodes()) {
    		node.removeChild(node.lastChild);
		}
	}

	function getChildByAttributeValue(element, attribute, value) {
	   var children = element.children;
	   for(var i = 0; i < children.length; i++) {
				var child = children[i];
				if(child.getAttribute(attribute) == value) {
				   return child;
				}
		}
		return null;
	}
	
	function getParams() {
      var params = new Object();
      var form = document.getElementById("oseeAppForm");
      var inputElements = form.getElementsByTagName("input");
      
      for(var i = 0; i < inputElements.length; i++) {
      var inputElement = inputElements[i];
         var id = inputElement.getAttribute("id");
         if (id == null) {
            console.log(inputElement);
            console.log("has no id");
         } else {
            params[id] = inputElement.value;
         }
      }
      if (document.getElementById("changeType")) {
	     params["changeType"] = document.getElementById("changeType").value
      }
      if (document.getElementById("priority")) {
	     params["priority"] = document.getElementById("priority").value
      }
      if (document.getElementById("desc")) {
	     params["desc"] = document.getElementById("desc").value
      }
      return params;
   }