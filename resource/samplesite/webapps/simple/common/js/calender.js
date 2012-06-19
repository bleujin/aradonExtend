function Calender(){
	this.isViewTime = false ;
	this.relativePath = '../../' ;
	this.innerFrame = null ;
	this.textBoxName = '' ;
	this.xPos = null;
	this.yPos = null;

	this.currDate = new Date(); 

	this.makeIframe = function(leftPos, topPos) {
		this.innerFrame = document.createElement("IFRAME");
		this.innerFrame.id = "calendarObj";
		this.innerFrame.name = "calendarObj";
		this.innerFrame.frameBorder = 0;
		this.innerFrame.src="../../common/page/blank.html";
	
		this.innerFrame.marginWidth=0 ;
		this.innerFrame.marginHeight=0 ;
		
		this.innerFrame.style.position = "absolute";
		this.innerFrame.style.display = "none";
		this.innerFrame.style.left = leftPos;
		this.innerFrame.style.top = topPos;
		this.innerFrame.style.width = "170px";
		this.innerFrame.style.height = "170px";
	
		document.body.appendChild( this.innerFrame);
	};
		
	this.callMethod = function(yyyymmdd){
		document.forms[0].elements[this.textBoxName].value = yyyymmdd ;
	} ;

	this.open = function(evt, callMethod, isViewTime, relativePath, xPos, yPos){
		// not use open.. maybe open is reerved word..
		
		if (arguments.length >= 6) this.yPos = yPos ;
		if (arguments.length >= 5) this.xPos = xPos ;
		if (arguments.length >= 4) this.relativePath = relativePath ;
		if (arguments.length >= 3) this.isViewTime = isViewTime ;
		if (arguments.length >= 2) this.callMethod = callMethod ;
		if (isIE()) evt = window.event;	
		
		if( this.innerFrame == null )
			this.makeIframe(0,0);
	
		if(this.xPos){
			x = this.xPos;
		} else {
			x = evt.clientX + document.body.scrollLeft + 10 ;	
		}
	
		if(this.yPos){
			y = this.yPos;
		} else {
			y = evt.clientY + document.body.scrollTop - 50 ;
		}		
		
		x = x+'px';
		y = y+'px';
		
		document.getElementById("div_calendar").style.top = y ;
		document.getElementById("div_calendar").style.left = x;
		
		this.innerFrame.style.left = x;
		this.innerFrame.style.top =  y;
		
		this.show() ;
	};
	
	this.openWithBox = function(evt, textBoxName, isViewTime, relativePath, xPos, yPos){
		this.textBoxName = textBoxName ;
		
		if (arguments.length == 6) {
			this.open(evt, this.callMethod, isViewTime, relativePath, xPos, yPos) ;
		} if (arguments.length == 4) {
			this.open(evt, this.callMethod, isViewTime, relativePath) ;
		} else if (arguments.length == 3) {
			this.open(evt, this.callMethod, isViewTime) ;
		} else { 
			this.open(evt, this.callMethod) ;
		}
	};

	this.getName  = function(){
		return 'Calender';
	};

	this.changeDay = function(changeType){
		if (changeType == -2) {
			this.currDate.addYear(-1) ;
		} else if ( changeType == -1) {
			this.currDate.addMonth(-1) ;
		} else if (changeType == 1 ) {
			this.currDate.addMonth(1) ;
		} else if (changeType == 2 ) {
			this.currDate.addYear(1) ;
		} else {
			return ;
		}
	
		this.show();
	};

	this.mouseOver = function(evt) {
		if (isIE()) evt = window.event;
		var el = (evt.srcElement) ? evt.srcElement : evt.target;
		if (el.title.isNotBlank()) {
			el.style.backgroundColor='#FFFF00';
		}
	};
	
	this.mouseClick = function(evt) {
		if (isIE()) evt = window.event;
		var el = (evt.srcElement) ? evt.srcElement : evt.target;
		var selDay = el.title;
		if (selDay.isNotBlank()) {
			if (this.isViewTime == true ){
				selDay  += "-" + document.getElementById("cal_sel_hour").value + "" + document.getElementById("cal_sel_min").value  + "00"
			}
			this.callMethod(selDay);
			document.getElementById("div_calendar").style.display='none';
			this.innerFrame.style.display='none';
		}
	};
	
	this.mouseOut = function(evt) {
		if (isIE()) evt = window.event;
		var el = (evt.srcElement) ? evt.srcElement : evt.target;
		
		if (el.title.isNotBlank()) {
			if ( el.className  =='css_sun') {
				el.style.backgroundColor = '#FFDFDF';
			} else if ( el.className =='css_sat' ) {
				el.style.backgroundColor = '#DDF1FF';
			} else {
				el.style.backgroundColor = '#FFFFFF';
			}
		}
	};
	
	this.show = function(){
		var str = "<table width='170' border='0' cellspacing='2' cellpadding='0' bgcolor='#333333' id='cal_table' class='cal_table' onSelectstart='return false' onSelect='return false' ";
		str += "  	onmouseover='" + this.getName() + ".mouseOver(event)' onmouseout='" + this.getName() + ".mouseOut(event)' ondblclick='" + this.getName() + ".mouseClick(event)' > \n";
		str += "  <tr> \n";
		str += "    <td bgcolor='#FFFFFF'> \n";
		str += "      <table width='100%' border='0' cellspacing='0' cellpadding='0'> \n";
		str += "        <tr>  \n";
		str += "          <td>  \n";
		str += "            <table width='100%' border='0' cellspacing='0' cellpadding='0'> \n";
		str += "              <tr>  \n";
		str += "                <td>  \n";
		str += "                  <table border='0' cellspacing='1' cellpadding='0'> \n";
		str += "                    <tr>  \n";
		str += "                      <td colspan='2'><img src='" + this.relativePath + "common/img/d_year.gif'></td> \n";
		str += "                    </tr> \n";
		str += "                    <tr>  \n";
		str += "                      <td><a href='javascript:" + this.getName() + ".changeDay(-2)'><img src='" + this.relativePath + "common/img/d_prev.gif' border=0></a></td> \n";
		str += "                      <td><a href='javascript:" + this.getName() + ".changeDay(2)'><img src='" + this.relativePath + "common/img/d_next.gif' border=0></a></td> \n";
		str += "                    </tr> \n";
		str += "                  </table> \n";
		str += "                </td> \n";
		str += "                <td width='100%' align='center'><font face='Tahoma' size='1'><b>" + this.currDate.getStrYear() + " / " + this.currDate.getStrMonth() + "</b></font></td> \n";
		str += "                <td>  \n";
		str += "                  <table border='0' cellspacing='1' cellpadding='0'> \n";
		str += "                    <tr>  \n";
		str += "                      <td colspan='2'><img src='" + this.relativePath + "common/img/d_month.gif' width='33' height='9'></td> \n";
		str += "                    </tr> \n";
		str += "                    <tr>  \n";
		str += "                      <td><a href='javascript:" + this.getName() + ".changeDay(-1)'><img src='" + this.relativePath + "common/img/d_prev.gif' border=0></a></td> \n";
		str += "                      <td><a href='javascript:" + this.getName() + ".changeDay(1)'><img src='" + this.relativePath + "common/img/d_next.gif' border=0></a></td> \n";
		str += "                    </tr> \n";
		str += "                  </table> \n";
		str += "                </td> \n";
		str += "              </tr> \n";
		str += "            </table> \n";
		str += "          </td> \n";
		str += "        </tr> \n";
		str += "        <tr>  \n";
		str += "          <td><img src='" + this.relativePath + "common/img/d_line.gif' width='100%' height='1'></td> \n";
		str += "        </tr> \n";
		str += "        <tr> \n";
		str += "          <td> \n";
		str += "            <table width='100%' border='0' cellspacing='1' cellpadding='1'> \n";
		str += "              <tr bgcolor='#FFFFFF' align='center'>  \n";
		str += "                <td class='cal_week'><font size='1' face='Verdana, Tahoma'>S</font></td> \n";
		str += "                <td class='cal_week'><font size='1' face='Verdana, Tahoma'>M</font></td> \n";
		str += "                <td class='cal_week'><font size='1' face='Verdana, Tahoma'>T</font></td> \n";
		str += "                <td class='cal_week'><font size='1' face='Verdana, Tahoma'>W</font></td> \n";
		str += "                <td class='cal_week'><font size='1' face='Verdana, Tahoma'>T</font></td> \n";
		str += "                <td class='cal_week'><font size='1' face='Verdana, Tahoma'>F</font></td> \n";
		str += "                <td class='cal_week'><font size='1' face='Verdana, Tahoma'>S</font></td> \n";
		str += "              </tr> \n";
		str += "              <tr bgcolor='#FFFFFF'> \n";
		str += "                <td colspan='7'><font size='1' face='Verdana, Tahoma'><img src='" + this.relativePath + "common/img/d_line2.gif' width='100%' height='1'></font></td> \n";
		str += "              </tr> \n";
	
		count_day = 1;
		lastDay = this.currDate.getLastDate() ;
		
		weekCount = 0;
	
		for ( week=0 ; week < 6 ; week++ ) {
			weekCount++;
			str += "<tr height='12'>"
			for (dayOfWeek=0; dayOfWeek <= 6 ; dayOfWeek++) {
				thisDate = new Date(this.currDate.getFullYear(), this.currDate.getMonth(), count_day) ;
	
				if (dayOfWeek == 0 ) {
					small_css = "css_sun";
				} else if ( dayOfWeek == 6) {
					small_css = "css_sat";
				} else {
					small_css = "css_def";
				}
	
				if ( dayOfWeek < thisDate.getDay() ) {
					str += "<td class='" + small_css + "'>&nbsp;</td>"
				} else {
					if (count_day > lastDay) {
						str += "<td class='" + small_css + "'>&nbsp;</td>"
						week = 10;
					} else {
						str += "<td class='" +  (thisDate.isToday() ? "css_tod" : small_css ) + "' title='" + this.currDate.getStrYear() + this.currDate.getStrMonth() + count_day.lpad(2) + "' onmouseover=\"this.style.backgroundColor='#FFFF00'\">" + count_day + "</td>"
					}
					if(lastDay == count_day) {
						week = 10 ;
					}
					count_day++;
				}
			}
			str += "</tr>\n"
		}
	
		if (this.isViewTime){
			str += this.showTime();
		}
	
		str += "            </table> \n";
		str += "          </td> \n";
		str += "        </tr> \n";
		str += "        <tr>  \n";
		str += "          <td><img src='" + this.relativePath + "common/img/d_line.gif' width='100%' height='1'></td> \n";
		str += "        </tr> \n";
		str += "        <tr> \n";
		str += "          <td align='center' height='20'><font size='1' face='Tahoma'><a href='javascript:" + this.getName() + ".hide()'><b>Cancel</b></a></font></td> \n";
		str += "        </tr> \n";
		str += "      </table> \n";
		str += "    </td> \n";
		str += "  </tr> \n";
		str += "</table> \n";
	
		document.getElementById("div_calendar").innerHTML = str;
		document.getElementById("div_calendar").style.display='';
		this.innerFrame.style.height = document.getElementById("div_calendar").offsetHeight;
		this.innerFrame.style.width = document.getElementById("div_calendar").offsetWidth;
		this.innerFrame.style.display='';
	};
	
	this.showTime = function(){
		var currMin = this.currDate.getMinutes().lpad(2).substring(0,1) + '0';
		
		str  = " <tr bgcolor='#FFFFFF'> \n";
		str += " <td colspan='7' align='center' NOWRAP> <b > </b > \n";
		str += " <select id='cal_sel_hour' class='cal_td'>\n";
	
		for ( h=0; h <24 ; h++ ){
			str += " <option value='" + h.lpad(2) + "' " + (h.lpad(2) == this.currDate.getStrHour() ? "selected" : "" )+ ">" + h + "</option>\n";
		}
		str += " </select> H \n";
		
		str += " <select id='cal_sel_min' class='cal_td'>\n";
		for ( h=0; h <60 ; h += 10 ){
			str += " <option value='" + h.lpad(2) + "' " + ( h.lpad(2) == currMin ? "selected" : "" )+ ">" + h + "</option>\n"; 
		}
		str += " </select> M</td>\n";
		str += " </tr>\n";
		return str;
	};
	
	this.hide = function(){
		document.getElementById("div_calendar").style.display='none';
		this.innerFrame.style.display='none';
	};
}

//////////////////////////////////////////////////////////////////////////////////////
//str = " <style> \n";
//str += " 	.cal_table {font-size: 8pt; background-color: #666666; color: #333333;cursor:hand;cursor:pointer };  \n";
//str += " 	.cal_td    {font-size: 8pt }; \n";
//str += " 	.cal_week  {font-size: 8pt; background-color: #CCCCCC;text-align: center;cursor:hand };  \n";
//str += " 	.css_sun   {font-size: 8pt; background-color: #FFDFDF; color: #FF0000 ;text-align: center;cursor:hand;cursor:pointer };  \n";
//str += " 	.css_sat   {font-size: 8pt; background-color: #DDF1FF; color: #003399 ;text-align: center;cursor:hand;cursor:pointer };  \n";
//str += " 	.css_def   {font-size: 8pt; background-color: #FFFFFF; color: #000000 ;text-align: center;cursor:hand;cursor:pointer };  \n";
//str += " 	.css_tod   {font-size: 8pt; background-color: #FFFFFF; color: #0000FF ;text-align: center;cursor:hand;cursor:pointer;font-weight: bold };  \n";
//str += " </style> \n";

str = " <div id='div_calendar' style='background:buttonface;width:100;display:none;position: absolute; z-index:99' onSelect='return false'>";
str += " </div> \n";

document.write(str)
Calender = new Calender() ;
