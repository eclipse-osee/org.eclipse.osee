		function startStopListener() {
			var button = document.getElementById("startToggleId");
			if (button.value == "Start") {
				appStart();
			} else {
				appCancel();
			}
		}
		
		function appCancel() {
		   window.stop();
		   var button = document.getElementById("startToggleId");
	   	button.value = "Start";
		}
		
		function appStart() {
			removeChildren(document.getElementById("log"));
			var button = document.getElementById("startToggleId");
			button.value = "Cancel";
		   oseeAppStart(getOseeAppParams());
		   
		}
		
		function getOseeAppParams() {
			var params = new Object();
			var form = document.getElementById("oseeAppForm");
			var inputElements = form.querySelectorAll("input, select"); 
			
			for(var i = 0; i < inputElements.length; i++) {
				var inputElement = inputElements[i];
				if(inputElement.getAttribute("type") == "radio") {
					   if (inputElement.checked) {
							params[inputElement.getAttribute("name")] = inputElement.nextSibling.nodeValue;
						}
				} else {
					var id = inputElement.getAttribute("id");
				
					if (id == null) {
						console.log(inputElement);
						console.log("has no id");
					} else {
						if(inputElement.hasAttribute("list")) {
						    var datalist = document.getElementById(inputElement.getAttribute("list"));
						    var selection  = inputElement.value;
						    var child = getChildByAttributeValue(datalist, "value", selection);
						    if(child == null) {
						        params[id] = null;
						    } else {
						        params[id] = child.getAttribute("guid");
						    }
						} else {
						    params[id] = inputElement.value;
						}
					}
				}
			}
			return params;
		}

		var button = document.getElementById("startToggleId");
		button.addEventListener("click", startStopListener);
