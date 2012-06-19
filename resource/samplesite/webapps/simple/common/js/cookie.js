	function xcookie() {
		this.setCookie = function(_name, _value, _expiredays, _path){
			// name, value, [expiredays], [path] ;
			var args = arguments.length ;
			var name = '' ;
			var value = '' ;
			var expires = '' ;
			var path = '' ;
			var todayDate = new Date();
			
			if (args < 2){
				alert('Usage : setCookie(name, value, [expiredays], [path])') ;
				return ;
			}else {
				name = _name + '=' ;
				value = escape(_value) ;
				path = '; path=/' ;
				expires = ';' ;
			}
			
			if (_expiredays != null){
				path = '; path=/' ;
				todayDate.setDate( todayDate.getDate() + _expiredays);
				expires = '; expires=' + todayDate.toGMTString() + ';' ;
			}
			if (_path != null){
				path = '; path=' + _path ;
			}
			
			// alert(name + value + path + expires ) ;
			document.cookie = name + value + path + expires ;
		};
		
		this.getCookie = function(uName) {
			var flag = document.cookie.indexOf(uName+'=');
			if (flag != -1) { 
				flag += uName.length + 1 ;
				end = document.cookie.indexOf(';', flag)  ;
		
				if (end == -1) end = document.cookie.length ;
				return unescape(document.cookie.substring(flag, end)) ;
			}
			return '';
		};
		
		this.isContain = function(uName){
			var flag = document.cookie.indexOf(uName+'=');
			if (flag != -1) { 
				return true ;
			}
			return false ;	
		};
		
		this.isCookieAllowed = function(){
			// for explore 
			if(Common.Browser.ie){
				return navigator.cookieEnabled ;
			}
			// for netscape
			else{
				document.cookie = 1 ;
				if(document.cookie) return true ;
				else return false ;	
			}
		};
	}
	cookie = new xcookie() ;
