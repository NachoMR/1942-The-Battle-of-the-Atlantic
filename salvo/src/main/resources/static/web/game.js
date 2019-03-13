//Creation of the two grids (ship's location and Salvoes) by means of function renderTable()
var colNumbers = ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10"];
var rowLetters = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"];
renderShipsTable();
renderSalvoesTable();

//getting the active/current gameplayer id from the "query string" at the end of the URL http://localhost:8080/web/game.html?gp=3
var gp = window.location.search.split("=")[1]
console.log("Vamos a hacer Fetch sobre: http://localhost:8080/api/game_view/" + gp);
console.log("window.location es: " + window.location);

//================ VUE VAR DECLARATION ===================

var myVue = new Vue({
	el: "#app",
	data: {
		items: [3, 12],
		droppedId: "",
		droppedSize: "",
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
		pastSalvoLocations: []
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
		fetch('/api/game_view/' + gp, {
				method: 'GET'
			})
			.then(function (response) {
				if (!response.ok) {
        throw new Error("HTTP error, status = " + response.status);
      	}
				return response.json();
				alert("Your Game Info Status: " + response.status);
			})
			.then(function (game_view) {
				if(game_view.error){
					alert(game_view.error);
					window.location.href = "games.html";
				}
			else{
				//console.log(game_view);
				myVue.game_view = game_view;
				showShipsOnGrid(myVue.game_view.ships);
				myVue.showSalvoesOnGrid(myVue.game_view.salvoes);
				myVue.checkForShips();
				myVue.getPastSalvoLocations();
				
				}
			})
			.catch(function (error) {
				alert(error);
			});			
	},		
		postShips: function(){
			//creating the objecto to be sent as the Body of the Fetch
			this.placedShips[0].locations = searchGridForClass("shipLocations", "carrier");
			this.placedShips[1].locations = searchGridForClass("shipLocations", "battleship");
			this.placedShips[2].locations = searchGridForClass("shipLocations", "submarine");
			this.placedShips[3].locations = searchGridForClass("shipLocations", "destroyer");
			this.placedShips[4].locations = searchGridForClass("shipLocations", "patrol_boat");
			//console.log(this.placedShips);
			if(this.placedShips[0].locations.length == 0 || this.placedShips[1].locations.length == 0 || this.placedShips[2].locations.length == 0 || this.placedShips[3].locations.length == 0 || this.placedShips[4].locations.length == 0){
				alert("You must place All five Warships on your Grid before start firing!")
			}
			else{				
				alert("Listo para enviar al Back-End...");
					
				fetch('/api/games/players/' + gp + '/ships', {
						credentials: 'include',
						headers: {
								'Content-Type': 'application/json'
						},
						method: 'POST',
						body: JSON.stringify(this.placedShips)
					})
				.then(function(response){				
					alert("Adding Ships status" + response.status);
					//return response.json();					
				})
				.then(function (data) {
					document.location.href='/web/game.html?gp=' + gp;
				})
				.catch(function (error) {
					console.log('Request failure: ', error);
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
					alert("Adding Salvo status" + response.status);
					//return response.json();					
				})
				.then(function (data) {
					//alert("Vamos a recargar la pagina");
					document.location.href='/web/game.html?gp=' + gp;
				})
				.catch(function (error) {
					console.log('Request failure: ', error);
				});
			
			
		},
		rotateShips: function(){
			if(this.horizontal){				document.getElementById("shipsToBePlaced").classList.toggle("row");
				for( var i = 0; i < this.allships.length; i++){
					var ship = document.getElementById(this.allships[i]);
					//console.log(ship);
					ship.setAttribute("class", this.allships[i] + "DraggableVertical mt-2 mr-2");
				}
				this.horizontal = false;
			}
			else{				document.getElementById("shipsToBePlaced").classList.toggle("row");
				for( var i = 0; i < this.allships.length; i++){
					var ship = document.getElementById(this.allships[i]);
					ship.setAttribute("class", this.allships[i] + "Draggable my-2");
				}
					this.horizontal = true;
			}
		},		
		getPastSalvoLocations: function(){
			this.game_view.salvoes
				.filter(salvo => salvo.gamePlayer == gp)
				.forEach(salvo => salvo.locations.forEach(loc => this.pastSalvoLocations.push(loc))
			);	
		},
		setSalvoLocation: function(ev){
			console.log(ev.target.id);
			var td = ev.target;
			var tdId = 	ev.target.id;
			var currentSalvoLocations = this.currentSalvo.locations;
			if(!this.pastSalvoLocations.includes(tdId.slice(5,8))){
				if(!td.classList.contains("salvoes")){
					if(currentSalvoLocations.length < 5){
						currentSalvoLocations.push(tdId.slice(5, 8));
						td.classList.add("salvoes");
						console.log(this.currentSalvo.locations);
					}
					else{
						alert("You can only fire 5 times in this turn")
					}
				}
				else{
					var index = currentSalvoLocations.indexOf(tdId.slice(5, 8));
					console.log("index is: ", index);
					currentSalvoLocations.splice(index, 1);
					td.classList.remove("salvoes");
					console.log(this.currentSalvo.locations);
				}
			}
			else{
				alert("You cannot select past salvoes locations");
			}
		},
		showSalvoesOnGrid: function(salvoes){
	salvoes.forEach(salvo => {
		if(salvo.gamePlayer == gp){			
			salvo.locations.forEach(location => {
					for(var i = 0; i < salvo.hits.length; i++){
						if(Object.keys(salvo.hits[i])[0] == location){
							document.getElementById("salvo" + location).classList.remove("missed");
							document.getElementById("salvo" + location).classList.add("hit");
							document.getElementById("salvo" + location).innerHTML = salvo.turn;
							//SWITCH Statement for all five ship types...
							//for one ship type:
							//var newHit = document.createElement("div");
							//newHit.classList.add("hit");
							//document.getElementById("myShipsStatus")....appendChild(newHit);
							console.log("Hit on ship type: " + salvo.hits[i][location] + " at " + location + " on turn " + salvo.turn);							
							break;
						}
						else{							
							document.getElementById("salvo" + location).classList.add("missed");
							document.getElementById("salvo" + location).innerHTML = salvo.turn;
						}
					}
			})
		}			
		else{
			salvo.locations.forEach(location => {
				for(var i = 0; i < salvo.hits.length; i++){
						if(Object.keys(salvo.hits[i])[0] == location){
							document.getElementById("ship" + location).classList.remove("missed");
							document.getElementById("ship" + location).classList.add("hit");
							document.getElementById("ship" + location).innerHTML = salvo.turn;
							console.log("Hit on ship type: " + salvo.hits[i][location] + " at " + location + " on turn " + salvo.turn);
							break;
						}
						else{								
							document.getElementById("ship" + location).classList.add("missed");
							document.getElementById("ship" + location).innerHTML = salvo.turn;
						}
				}
			})
		}
	});
},
			//drag&drop methods
		dragStart: function(ev) {
			console.log("The dragStart 'ev' is: ");
			console.log(ev);
  		ev.dataTransfer.setData("text", ev.target.id);
  		ev.dataTransfer.setData("size", ev.target.dataset.length);
			//console.log("la longitud es: " + ev.target.dataset.length);
		},
		dragOver: function(ev) {
  		ev.preventDefault();			
		},
		drop: function(ev) {
			console.log("The drop 'ev' is: ");
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
					cellId.removeAttribute("v-on:dragover");
					cellId.removeAttribute("v-on:drop");
			}
			},
		
		/*
		showHits(gamePlayer){
			for(var turn = 1; i <= this.game_view.gamePlayer.length; i++){
				for(var j = 0; j < hits.length; j++){
					var td = document.getElementById(hits[j].shiptype + "Hits").tr.children;
				}
			}
		}
		*/
		
	},
	created: function(){
		this.getGameView();
	},
	computed: {}
});

// ====================== FUNCTIONS ======================

function renderShipsTable(){
	document.getElementById("shipLocations").innerHTML = "<tr><td></td>" + colNumbers.map(col => "<td>" + col + "</td>").join("") + "</tr>" + rowLetters.map(row => "<tr><td>" + row + "</td>" + colNumbers.map(col => "<td id='ship" + row + col + "' v-on:dragover='dragOver(this.event)' v-on:drop='drop(this.event)'>" + "</td>").join("") + "</tr>").join("");
}

function renderSalvoesTable(){
	document.getElementById("salvoesLocations").innerHTML = "<tr><td></td>" + colNumbers.map(col => "<td>" + col + "</td>").join("") + "</tr>" + rowLetters.map(row => "<tr><td>" + row + "</td>" + colNumbers.map(col => "<td id='salvo" + row + col + "'v-on:click='setSalvoLocation(this.event)'>" + "</td>").join("") + "</tr>").join("");
}

function showShipsOnGrid(ships) {
	ships.forEach(ship => ship.locations.forEach(loc => {
	document.getElementById("ship" + loc).setAttribute("class", ship.type);
	//document.getElementById("ship" + loc).innerHTML = ship.type.charAt(0).toUpperCase();
	})
	);
}
/*
function showSalvoesOnGrid(salvoes){
	salvoes.forEach(salvo => {
		if(salvo.gamePlayer == gp){			
			salvo.locations.forEach(location => {
					for(var i = 0; i < salvo.hits.length; i++){
						if(Object.keys(salvo.hits[i])[0] == location){
							document.getElementById("salvo" + location).classList.remove("missed");
							document.getElementById("salvo" + location).classList.add("hit");
							break;
						}
						else{								
								document.getElementById("salvo" + location).classList.add("missed");
						}
					}
			})
		}			
		else{
			salvo.locations.forEach(location => {
				for(var i = 0; i < salvo.hits.length; i++){
						if(Object.keys(salvo.hits[i])[0] == location){
							document.getElementById("ship" + location).classList.remove("missed");
							document.getElementById("ship" + location).classList.add("hit");
							break;
						}
						else{								
								document.getElementById("ship" + location).classList.add("missed");
						}
				}
				
				
				//document.getElementById("ship" + location).classList.add("salvoes"));
				
			})
		}
	});
}
*/
function searchGridForClass(targetTable, targetClass){
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











