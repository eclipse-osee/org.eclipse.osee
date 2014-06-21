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