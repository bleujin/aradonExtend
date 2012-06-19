function xbrowser() {
	var agent = navigator.userAgent.toLowerCase();
	this.major = parseInt(navigator.appVersion);
	this.minor = parseFloat(navigator.appVersion);

	this.ns  = ((agent.indexOf('mozilla')!=-1) && ((agent.indexOf('spoofer')==-1) && (agent.indexOf('compatible') == -1)));
	this.ns2 = (this.ns && (this.major == 2));
	this.ns3 = (this.ns && (this.major == 3));
	this.ns4b = (this.ns && (this.major == 4) && (this.minor <= 4.03));
	this.ns4 = (this.ns && (this.major >= 4));

	this.ie   = (agent.indexOf("msie") != -1);
	this.ie3  = (this.ie && (this.major < 4));
	this.ie4  = (this.ie && (this.major < 5));
	this.ie5  = (this.ie && (this.major < 5.5));
	this.ie55  = (this.ie && (this.major >= 5.5));

	this.op3 = (agent.indexOf("opera") != -1);
	this.chkAjaBrowser = chkAjaBrowser ;
}

// var xbrowser = new xbrowser()


function chkAjaBrowser()
{
	var a,ua = navigator.userAgent;
	this.bw= { 
	  safari    : ((a=ua.split('AppleWebKit/')[1])?a.split('(')[0]:0)>=124 ,
	  konqueror : ((a=ua.split('Konqueror/')[1])?a.split(';')[0]:0)>=3.3 ,
	  mozes     : ((a=ua.split('Gecko/')[1])?a.split(" ")[0]:0) >= 20011128 ,
	  opera     : (!!window.opera) && ((typeof XMLHttpRequest)=='function') ,
	  msie      : (!!window.ActiveXObject)?(!!createHttpRequest()):false 
	}
	return (this.bw.safari||this.bw.konqueror||this.bw.mozes||this.bw.opera||this.bw.msie)
}