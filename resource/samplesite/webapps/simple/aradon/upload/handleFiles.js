function handleDrop(event)
{
	preventDef(event)
	
    var dt = event.dataTransfer;

  	var files = dt.files;
    
	for(var i = 0; i < files.length;i++)
  	{
    	
		http_request = new XMLHttpRequest();
		var boundaryString = 'boundery--';
		var boundary = '--' + boundaryString;
		var requestbody = boundary + '\n' 
		
		+ 'Content-Disposition: form-data; name="thefilename"' + '\n' 
		+ '\n' 
		+ files[i].fileName + '\n' 
		+ '\n' 
		+ boundary + '\n' 
		+ 'Content-Disposition: form-data; name="thefile"; filename="' 
			+ files[i].fileName + '"' + '\n' 
		+ 'Content-Type: application/octet-stream' + '\n' 
		+ '\n'
		+ files[i].getAsBinary()
		+ '\n'
		+ boundary;
	
		http_request.onreadystatechange = _handleReadyState;
		http_request.open('POST', 's.php', true);
		http_request.setRequestHeader("Content-type", "multipart/form-data; \
			boundary=\"" + boundaryString + "\"");
		http_request.setRequestHeader("Connection", "close");
		http_request.setRequestHeader("Content-length", requestbody.length);
		http_request.sendAsBinary(requestbody);  	
    }
}

function _handleReadyState()
{
    if (this.readyState == 4)
    {
        if(this.status == 200)
        {
            var pathStored = this.responseText
            document.getElementById('status-msg').innerHTML += ' <br />';

            var linkToUpload = document.createElement('a');
            linkToUpload.href = pathStored;
            linkToUpload.appendChild(document.createTextNode(pathStored));
            document.getElementById('status-msg').appendChild(linkToUpload);
        }
    }
}

/**
* Event handlers
*/

function preventDef(event)
{
	event.preventDefault();
	event.stopPropagation();
}

function setEventHandlers()
{

	var dropTarget = document.getElementById('file-drop-target');
	addEvent(dropTarget,'dragover',preventDef, true);
	addEvent(dropTarget,'dragenter',preventDef, true);
	addEvent(dropTarget,'drop',handleDrop, true);
}

function addEvent(obj, evType, fn, useCapture)
{
  if (obj.addEventListener){
    obj.addEventListener(evType, fn, useCapture);
    return true;
  } else if (obj.attachEvent){
      var r = obj.attachEvent("on"+evType, fn);
    return r;
  }
}

function addOnLoadEvent(sFunc)
{
    if(window.addEventListener)
        window.addEventListener('load', sFunc, false);
    else if(window.attachEvent)
        window.attachEvent('onload', sFunc);
}

addOnLoadEvent(setEventHandlers);