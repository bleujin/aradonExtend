function Database(session){
	this.session = session ;

	this.createUserCommand = function(queryCommand){
		return new UserCommand(this, queryCommand);
	} ;
	
	this.createUserProcedure = function(queryCommand){
		return new UserProcedure(this, queryCommand) ;
	} ;
	
	this.createUserProcedures = function(queryCommand){
		return new UserProcedures(this, queryCommand) ;
	} ;
	
	
	this.toString = function(){
		return this.path ;
	} ;
}


function Session(path, userId){
	this.path = path ;
	this.userId = userId ;
	
	this.jsonString = function(){
		return "session:{userId:'" + this.userId + "'}";
	}
}


function UserCommand(database, queryCommand){

	this.database = database ;
	this.query = new Query(queryCommand, 'usercommand', queryCommand) ;
	this.page = new Page(10, 1) ;
	this.params = new Params() ;
	
	this.getQueryType = function(){
		return query.type ;
	} ;

	this.setParam = function(paramName, paramValue){
		this.params.addParam(paramName, paramValue) ;
	} ;

	this.setPage = function(listNum, pageNo){
		this.page = new Page(listNum, pageNo) ;
	} ;
	
	this.execQuery = function(){
		return execute(this, 'select') ;
	} ; 

	function execute(queryObj, ctype){
		var aradon = new $net.ion.aradon.AradonClient() ;
		var targetLet = aradon.getSection('db').getLet('/query') ;
		targetLet.addParam('aradon.result.method', 'POST') ;
		targetLet.addParam('query', queryObj.jsonString(ctype)) ;
		var res = targetLet.post(
			function(res) { 
				// alert('inner', res.responseText) ;
				$('result').innerHTML += res  + '...<br/>\n';
				// return res ;
			} 
		) ;	
		
		// alert('outer', res.responseText) ;
		return res ;
		
		/*
		alert(targetLet) ;
		
		var aj = new ajax(queryObj.database.session.path, 'POST') ;
		aj.addParam('aradon.result.method', 'POST') ;
		aj.addParam('query', queryObj.jsonString(ctype)) ;
		alert(queryObj.jsonString(ctype)) ;

		aj.execMethod = function(parentThis, xmlRequest){
			parentThis.result = xmlRequest.responseText.trim() ;
			return parentThis ;
		}
		
		aj.connect(false) ;
		return aj.result ;
		*/
	}
	
	this.execUpdate = function(){
		return execute(this, 'dml') ;
	} ;
	
	
	this.jsonString = function(ctype) {
		return '{query:{' + this.query.jsonString(ctype)	+ ', ' + this.page.jsonString() + ', ' + this.params.jsonString() + '}}';
	}
	
	this.toString = function(){
		return this.queryCommand ;
	} ;
}


function UserProcedure(database, queryCommand){
	this.database = database ;
	this.query = new Query(queryCommand, 'userprocedure', queryCommand) ;
	this.page = new Page(0, 0) ;
	this.params = new Params() ;


}

UserProcedure.prototype = new UserCommand() ;



function UserProcedures(database, queryCommand){
	this.database = database ;
	this.query = new Query(queryCommand, 'userprocedures', queryCommand) ;
	this.page = new Page(0, 0) ;
	this.params = new Params() ;
	this.upts = new ArrayList() ;
	
	
	this.getQueryType = function(){
		return query.type ;
	} ;
		
	this.add = function(upt){
		this.upts.add(upt);
		return this ;
	} ;

	this.execQuery = function(){
		return execute(this, 'select') ;
	} ; 

	function execute(queryObj, ctype){
		if (queryObj.upts.size() <= 0) alert('Exception:', 'exception.db.userprocedures.EmptyQuery') ;
		
		var aj = new ajax(queryObj.database.session.path, 'POST') ;
		aj.addParam('write', true) ;
		aj.addParam('query', queryObj.jsonString(ctype)) ;
		aj.execMethod = function(parentThis, xmlRequest){
			parentThis.result = xmlRequest.responseText.trim() ;
			return parentThis ;
		}
		
		aj.connect(false) ;
		return aj.result ;
	}
	
	this.execUpdate = function(){
		return execute(this, 'dml') ;
	} ;
	
	
	this.jsonString = function(ctype) {
		var queryString = "name:\"" + this.query.name + "\", type:'" + this.query.type  + "', ctype:'" + ctype  + "', command:[";
		// command:\"" + this.query.command + "\", 
		for(var i=0, last=this.upts.size() ; i < last ; i++){
			if (i > 0) queryString += ', ' ;
			queryString += this.upts.get(i).jsonString() + '\n';	
		}
		
		
		queryString += ']' ;
		return '{query:{' + queryString + '}}';
	}
	
	this.toString = function(){
		return this.queryCommand ;
	} ;	
}







function Query(name, type, command) {
	this.name = name ;
	this.command = command ;
	this.type = type ;
	
	this.jsonString = function(ctype){
		return "name:\"" + this.name + "\", command:\"" + this.command + "\", type:'" + this.type  + "', ctype:'" + ctype  + "'";
	}
}

function Page(listnum, pageno){
	this.listnum = listnum ;
	this.pageno = pageno ;
	
	this.jsonString = function(){
		if (this.listnum <= 0 ) return "page:{}" ;
		
		return "page:{listnum:" + this.listnum + ", pageno:" + pageno + "}" ;
	}
}

function Params(){
	this.params = new $net.ion.util.Map() ;
	
	this.addParam = function(key, value){
		this.params.put(key, value) ;
	} ;
	
	this.jsonString = function(){
		var result = 'param:{' ;
		
		for(var i=0, last=this.params.size() ; i < last ; i++){
			if (i > 0) result += ', ' ;
			
			result += this.params.getEntry(i).key + ":" ;
			var value = this.params.getEntry(i).value ;
			if (typeof value != 'number') value = "\"" + value + "\"" ;
			
			result += value ;
		}
		
		result += '}';
		return result ;
	}
}

function ignoreTab(e){
	var evt = window.event ? window.event : e;
	var textbox = $('query');
	if(evt.keyCode==9){ // tab code
		// this.frm.stripProg.focus();
		space = "	"; //tab space
		textbox.selection = document.selection.createRange(); //cursor location
		textbox.selection.text = space;
		evt.returnValue = false;  
	}  
}