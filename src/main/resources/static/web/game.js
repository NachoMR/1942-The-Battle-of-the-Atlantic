//Creation of the two grids (ship's location and Salvoes) by means of function renderTable()
var colNumbers = ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10"];
var rowLetters = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"];
renderShipsTable();
renderSalvoesTable();

//getting the active/current gameplayer id from the "query string" at the end of the URL http://localhost:8080/web/game.html?gp=3
var gp = window.location.search.split("=")[1]
console.log("window.location es: " + window.location);

//================ VUE VAR DECLARATION ===================

var myVue = new Vue({
	el: "#app",
	data: {	
		currentState: 0,
		yourCurrentTurn: 0,
		opponentCurrentTurn: 0,
		droppedId: "",
		droppedSize: "",
		droppable: true,
		game_view: {},
		placingShips: false,
		horizontal: true,
		allships: ["carrier", "battleship", "submarine", "destroyer", "patrol_boat"],
		placedShips: [{type: "carrier", locations: []},
									{type: "battleship", locations: []},
									{type: "destroyer", locations: []},
									{type: "submarine", locations: []},		
									{type: "patrol_boat", locations: []}
									],
		currentSalvo: {turn: "", locations: []},
		pastSalvoLocations: [],
		modalAlertContent: ""
		},
	methods: {
		checkForShips: function(){
			if(this.game_view.ships.length == 0){
				this.placingShips = true;
			}
			else{
				this.placingShips = false;
			}			
		},
		goToHome: function(){
			document.location.href="/web/games.html"
		},
		reposition: function(){
			document.location.href = "/web/game.html?gp=" + gp;
		},
		getGameView: function () {
			console.log("El gp leido desde la url es: " + gp);
		fetch('/api/game_view/' + gp, {
				method: 'GET'
			})
			.then(function (response) {
				console.log(response);
				return response.json();				
			})
			.then(function (game_view) {
				if(game_view.error){
					myVue.showModalAlert(game_view.error);
					$("#exampleModal").on("hide.bs.modal", function(){
						window.location.href = "games.html";
					})
					//window.location.href = "games.html";
				}
			else{
				myVue.game_view = game_view;
				myVue.getCurrentTurns();
				myVue.currentState = game_view.CurrentGameState.stateCode;
				myVue.showShipsOnGrid(game_view.ships);
				myVue.checkForShips();
				myVue.getPastSalvoLocations();
				myVue.showSalvoesOnGrid(myVue.game_view.salvoes);
				//see private method checkState at salvoController.java to find out more about stateCode numbers, e.g. {8, "Waiting for your opponent to fire his/her Salvo"}
				if(	game_view.CurrentGameState.stateCode == 8 ){
					checkingForStateUpdate(game_view.CurrentGameState.stateCode);
				}		
			}
		})
			.catch(function (er) {
				console.log(er.name + " & " + er.message);
				myVue.showModalAlert(er.message);
			$("#exampleModal").on("hide.bs.modal", function(){
				window.location.href = "games.html";
			})
			});			
	},		
		postShips: function(){
			//creating the objecto to be sent as the Body of the Fetch
			this.placedShips[0].locations = searchGridForClass("shipLocations", "carrier");
			this.placedShips[1].locations = searchGridForClass("shipLocations", "battleship");
			this.placedShips[2].locations = searchGridForClass("shipLocations", "submarine");
			this.placedShips[3].locations = searchGridForClass("shipLocations", "destroyer");
			this.placedShips[4].locations = searchGridForClass("shipLocations", "patrol_boat");
			
			if(this.placedShips[0].locations.length == 0 || this.placedShips[1].locations.length == 0 || this.placedShips[2].locations.length == 0 || this.placedShips[3].locations.length == 0 || this.placedShips[4].locations.length == 0){
				myVue.showModalAlert("You must place All five Warships on your Grid before start firing!")
			}
			else{				
				fetch('/api/games/players/' + gp + '/ships', {
						credentials: 'include',
						headers: {
								'Content-Type': 'application/json'
						},
						method: 'POST',
						body: JSON.stringify(this.placedShips)
					})
				.then(function(response){				
					console.log(response);
					return response.json();					
				})
				.then(function (data) {
					if(data.error){
						myVue.showModalAlert(data.error);
						$("#exampleModal").on("hide.bs.modal", function(){
							document.location.reload();
						})
					}
					document.location.reload();
					//document.location.href='/web/game.html?gp=' + gp;
				})
				.catch(function (er) {
					//console.log('Request failure: ', er);
					myVue.showModalAlert(er.message);
					$("#exampleModal").on("hide.bs.modal", function(){
						document.location.reload();
						})
				});
			}
		},
		postSalvo: function(){			
			//add code to POST myVue.currentSalvo to the Back-End
			fetch('/api/games/players/' + gp + '/salvoes', {
						credentials: 'include',
						headers: {
								'Content-Type': 'application/json'
						},
						method: 'POST',
						body: JSON.stringify(this.currentSalvo)
					})
				.then(function(response){				
					console.log(response);
					return response.json();					
				})
				.then(function (data) {
					if(data.error){
						myVue.showModalAlert(data.error);
						$("#exampleModal").on("hide.bs.modal", function(){
							document.location.href='/web/game.html?gp=' + gp;
						})
					}
					document.location.reload();
				})
				.catch(function (er) {
					//console.log('Request failure: ', er);
					myVue.showModalAlert(er.message);
					$("#exampleModal").on("hide.bs.modal", function(){
						document.location.reload();
					})
				});
			
			
		},		
		getPastSalvoLocations: function(){
			this.game_view.salvoes
				.filter(salvo => salvo.gamePlayer == gp)
				.forEach(salvo => salvo.locations.forEach(loc => this.pastSalvoLocations.push(loc))
			);	
		},
		setSalvoLocation: function(ev){
			//console.log(ev.target.id);
			var td = ev.target;
			var tdId = 	ev.target.id;
			var currentSalvoLocations = this.currentSalvo.locations;
			if(!this.pastSalvoLocations.includes(tdId.slice(5,8))){
				if(!td.classList.contains("salvoes")){
					if(currentSalvoLocations.length < 5){
						currentSalvoLocations.push(tdId.slice(5, 8));
						td.classList.add("salvoes");
						
						//console.log(this.currentSalvo.locations);
					}
					else{
						myVue.showModalAlert("You can only fire 5 times in this turn")
					}
				}
				else{
					var index = currentSalvoLocations.indexOf(tdId.slice(5, 8));
					//console.log("index is: ", index);
					currentSalvoLocations.splice(index, 1);
					td.classList.remove("salvoes");
					//console.log(this.currentSalvo.locations);
				}
			}
			else{
				myVue.showModalAlert("You cannot select past salvoes locations");
			}
		},
		addHitToStatus: function(children, num, turn){
			var newHitDiv = document.createElement("div");
			newHitDiv.classList.add("hit");
			newHitDiv.innerHTML = turn;
			children[num].children[0].appendChild(newHitDiv);
			if(children[num].children[0].children.length == children[num].children[0].dataset.length){
				children[num].children[1].classList.add("strikethrough");
				children[num].children[0].classList.add("sinking");
			}
		},
		renderHitAndMissed: function(grid, shipsStatus, salvo, location){
			var tdId = document.getElementById(grid + location);
			var turn = salvo.turn;
			if(salvo.hits.length == 0){
				tdId.classList.add("missed");
				tdId.innerHTML = turn;
			}
			else{			
				for(var i = 0; i < salvo.hits.length; i++){
						//var tdId = document.getElementById(grid + location);
						//var turn = salvo.turn;
						if(Object.keys(salvo.hits[i])[0] == location){							
							tdId.classList.remove("missed");
							tdId.classList.add("hit");
							tdId.classList.add(salvo.hits[i][location]);
							tdId.innerHTML = turn;
							var type = salvo.hits[i][location];
							var children = document.getElementById(shipsStatus).children;
							//console.log(children);
							switch(type){
								case "patrol_boat":
									//index 4 as per game.html structure
									this.addHitToStatus(children, 4, turn);
									break;
								case "carrier":
									//index 3 as per game.html structure
									this.addHitToStatus(children, 3, turn);
									break;
								case "submarine":
									//index 2 as per game.html structure
									this.addHitToStatus(children, 2, turn);
									break;
								case "destroyer":
									//index 1 as per game.html structure
									this.addHitToStatus(children, 1, turn);
									break;
								case "battleship":
									//index 0 as per game.html structure
									this.addHitToStatus(children, 0, turn);
									break;
								default:
									console.log("Error: Ship type not found for this Hit");
							}							
							//console.log("Hit on ship type: " + salvo.hits[i][location] + " at " + location + " on turn " + salvo.turn);							
							break;
						}
						else{							
							tdId.classList.add("missed");
							tdId.innerHTML = salvo.turn;
						}
				}
			}
		},
		showSalvoesOnGrid: function(salvoes){
			salvoes.forEach(salvo => {
				if(salvo.gamePlayer == gp){
					salvo.locations.forEach(location => {
						this.renderHitAndMissed("salvo","opponentShipsStatus", salvo, location);
					})
				}			
				else{
					salvo.locations.forEach(location => {
						this.renderHitAndMissed("ship", "myShipsStatus", salvo, location);
					})
				}
			});
		},
		showShipsOnGrid: function(ships) {
			ships.forEach(ship => ship.locations.forEach(loc => {
				document.getElementById("ship" + loc).setAttribute("class", ship.type);
				//document.getElementById("ship" + loc).innerHTML = ship.type.charAt(0).toUpperCase();
				})
			);
		},		
		getCurrentTurns: function(){			
			var player = this.game_view.gamePlayers[0].id;
			var salvoes = this.game_view.salvoes;
			if(salvoes.length > 0){
				this.yourCurrentTurn =  Math.max(...salvoes.filter(salvo => salvo.gamePlayer == player).map(salvo => salvo.turn));						
				
				if(this.game_view.gamePlayers.length > 1){
					var opponent = this.game_view.gamePlayers[1].id;
					this.opponentCurrentTurn = Math.max(...salvoes.filter(salvo =>	salvo.gamePlayer == opponent).map(salvo => salvo.turn));
				}
			}
		},
		//rotateShips just below needs REFACTOR to avoid DRY!!
		rotateShips: function(){
			if(this.horizontal){
				document.getElementById("shipsToBePlaced").classList.toggle("row");
				for( var i = 0; i < this.allships.length; i++){
					var ship = document.getElementById(this.allships[i]);
					//console.log(ship);
					ship.classList.toggle(this.allships[i] + "DraggableVertical");
					ship.classList.toggle(this.allships[i] + "Draggable");
				}
				this.horizontal = false;
			}
			else{
				document.getElementById("shipsToBePlaced").classList.toggle("row");
				for( var i = 0; i < this.allships.length; i++){
					var ship = document.getElementById(this.allships[i]);
					ship.classList.toggle(this.allships[i] + "DraggableVertical");
					ship.classList.toggle(this.allships[i] + "Draggable");
				}
					this.horizontal = true;
			}
		},
			//DRAG & DROP METHODS
		dragStart_handler: function(ev) {	
			console.log("event DRAGSTART:");
			console.log(ev);
			//console.log("ev.offsetX = " + ev.offsetX);
			//console.log("ev.clientX = "  + ev.clientX);
			//console.log("ev.pageX = "  + ev.pageX);
			//console.log("ev.screenX = "  + ev.screenX);			
  		ev.dataTransfer.setData("text", ev.target.id);
  		ev.dataTransfer.setData("size", ev.target.dataset.length);
			this.shipSize = ev.target.dataset.length;
		},		
		dragOver_handler: function(ev) {
			console.log("event DRAGOVER:");
			console.log(ev);
  		ev.preventDefault();
			ev.target.classList.add("greenish");
			
		},
		
		//****************************************
		allowDrop: function(ev){
				this.droppable = true;
			console.log("event ALLOWDROP:");
			console.log(ev);
			console.log(ev.target.id);
			//var draggedArr = [];
			//console.log(draggedArr);
			for(i = 0; i < this.shipSize; i++){
				if(this.horizontal){					
					if(parseInt(ev.target.id.slice(5,7))+i > 10){
						console.log("entra en el segundo if");
						console.log((parseInt(ev.target.id.slice(5,7))+i));
						this.droppable = false;
						break;
					}
					else if(this.allships.includes(document.getElementById("ship" + ev.target.id.slice(4,5) + (parseInt(ev.target.id.slice(5,7))+i)).classList[0])){
						console.log("entra en el primer if");
						this.droppable = false;
						break;
					}					
				}
				else{
					var letterIndex = rowLetters.indexOf(ev.target.id.slice(4, 5));
					if(letterIndex + i > 9){
						this.droppable = false;
						break;
					}
					else if(this.allships.includes(document.getElementById("ship" + (rowLetters[letterIndex + i]) + parseInt(ev.target.id.slice(5,7))).classList[0])){
						this.droppable = false;
						break;
					}
				}
				
			}
//			console.log("draggedArr: " + draggedArr);
//			console.log("Func",draggedArr.some(this.isInvalidDropArea));
//			
//			if( draggedArr.some(this.isInvalidDropArea) ){
//				this.droppable = false;
//				console.log("false");
//			}
//			else{
//				this.droppable = true;
//				console.log("true");
//			}
		},
		//****************************************
		
		isInvalidDropArea: function(loc){
			console.log("loc= " , loc);
			console.log("ship" , loc);
			console.log("SHIP CLASS" , document.getElementById("ship" + loc).classList);
			return document.getElementById("ship" + loc).classList.length > 1;
			//return false;	
			
			
		},
		dragLeave_handler: function(ev){
			console.log("event DRAGLEAVE:");
			console.log(ev);
			ev.target.classList.remove("greenish");
		},
		drop_handler: function(ev) {
			if(this.droppable == true){
				console.log("event DROP:" );
				console.log(ev);
				//console.log(ev.target.id.slice(4,7));
				ev.preventDefault();
				this.droppedId = ev.dataTransfer.getData("text");
				//console.log("The droppedId is: " + myVue.droppedId);
				this.droppedSize = ev.dataTransfer.getData("size");
				//console.log("The droppedSize is: " + myVue.droppedSize);
				document.getElementById(this.droppedId).classList.add("hidden");			
				for(var i = 0 ; i < this.droppedSize ; i++){								
					if(this.horizontal){
						var letterId = ev.target.id.slice(4, 5);
						var numberId = parseInt(ev.target.id.slice(5,7)) + i;					
					}
					else{
						var letterIndex = rowLetters.indexOf(ev.target.id.slice(4, 5))
						var letterId = rowLetters[letterIndex +i];
						var numberId = parseInt(ev.target.id.slice(5,7));					
					}
					var cellId = document.getElementById("ship" + letterId + numberId);
						//console.log("letter: " + letterId + " + number: " + numberId);
						//console.log(cellId);
						cellId.setAttribute("class", this.droppedId);
						//cellId.removeAttribute("v-on:dragover");
						//cellId.removeAttribute("v-on:drop");
				}
			}
			else{
				ev.target.classList.remove("greenish");
				myVue.showModalAlert("position not permitted");
			}			
		},
		mouseover_handler: function(ev){
			var td = ev.target;
			td.classList.add("yellow");
		},
		mouseout_handler: function(ev){
			var td = ev.target;
			td.classList.remove("yellow");
		},
		showModalAlert: function (text) {
			this.modalAlertContent = text;
			$('#exampleModal').modal('show');
			//this.modalAlertContent = "reset modalAlertContent after calling modal!!";
		},
		closeModalAlert: function () {
			$('#exampleModal').modal('hide');
			this.modalAlertContent = "Modal closed!";
		}
		
	},
	created: function(){
		this.getGameView();
	},
	computed: {}
});

// ====================== FUNCTIONS ======================

function renderShipsTable(){
	document.getElementById("shipLocations").innerHTML = "<tr><td></td>" + colNumbers.map(col => "<td>" + col + "</td>").join("") + "</tr>" + rowLetters.map(row => "<tr><td>" + row + "</td>" + colNumbers.map(col => "<td id='ship" + row + col + "' v-on:dragover='dragOver_handler(this.event); allowDrop(this.event)' v-on:drop='drop_handler(this.event)' v-on:dragleave='dragLeave_handler(this.event)'>" + "</td>").join("") + "</tr>").join("");
}

function renderSalvoesTable(){
	document.getElementById("salvoesLocations").innerHTML = "<tr><td></td>" + colNumbers.map(col => "<td>" + col + "</td>").join("") + "</tr>" + rowLetters.map(row => "<tr><td>" + row + "</td>" + colNumbers.map(col => "<td id='salvo" + row + col + "'v-on:click='setSalvoLocation(this.event)' v-on:mouseover='mouseover_handler(this.event)' v-on:mouseout='mouseout_handler(this.event)'>" + "</td>").join("") + "</tr>").join("");
}

function 	searchGridForClass(targetTable, targetClass){
	var table = document.getElementById(targetTable);
	var tr = table.getElementsByTagName("tr");
	var shipArray = [];
	for(var i = 0; i < tr.length; i++){
		var td = tr[i].getElementsByTagName("td")
		for(var j = 0; j < td.length; j++){
			if(td[j].classList.contains(targetClass)){				
				var letter = rowLetters[i-1];
				var number = j;
				//console.log("Letra+Numero: " + letter + number);								
				shipArray.push(letter + number);
			}
		}
	}
	return shipArray;
}

//Below we have two functions to check changes on the opponents salvoes every given interval (5s)
function salvoesUpdate(stateCode){
	fetch('/api/game_view/' + gp, {
				method: 'GET'
			})
			.then(function (response) {				
			//console.log("Your Game Info Status: " + response.status);
				return response.json();				
			})
			.then(function (game_view2) {					
				if(game_view2.CurrentGameState.stateCode != stateCode){
					console.log("Your turn.....");
					//clearInterval(timerId);
					document.location.href='/web/game.html?gp=' + gp;
				}
				else{
					console.log("Waiting........timerId= " + timerId);
					checkingForStateUpdate(stateCode);
				}
			})
			.catch(function (er) {
			  //console.log(er.message);
				myVue.showModalAlert(er.message);
				//window.location.href = "games.html";
			});	
}
var timerId;
var delay = 3000;
function checkingForStateUpdate(stateCode){
	timerId = setTimeout(salvoesUpdate, delay, stateCode);
}






//function checkingForStateUpdate(state){
//	console.log(state);
//		var timerId;
//	if(state == "Waiting for your opponent to fire his/her Salvo"){
//		console.log("waiting.......");
//		this.timerId = setTimeout(salvoesUpdate, 10000);
//	}
//	else{
//		console.log("Your turn......");
//		clearTimeout(timerId);		
//	}
//}

