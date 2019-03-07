//Creation of the two grids (ship's location and Salvoes) by means of function renderTable()
var colNumbers = ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10"];
var rowLetters = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"];
renderTable("ship", "shipLocations");
renderTable("salvo", "salvoesLocations");

//getting the active/current gameplayer id from the "query string" at the end of the URL http://localhost:8080/web/game.html?gp=3
var gp = window.location.search.split("=")[1]
console.log("Vamos a hacer Fetch sobre: http://localhost:8080/api/game_view/" + gp);
console.log("window.location es: " + window.location);



var placedShips = [{type: "carrier", locations: []},
									 {type: "patrol_boat", locations: []},
									 {type: "destroyer", locations: []},
									 {type: "submarine", locations: []},		
									 {type: "battleship", locations: []}
										];



//================ VUE VAR DECLARATION ===================

var myVue = new Vue({
	el: "#app",
	data: {
		droppedId: "",
		droppedSize: "",
		game_view: [],
		placeShips: true,
		horizontal: true,
		allships: ["carrier", "battleship", "submarine", "destroyer", "patrol_boat"]
	},
	methods: {
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
				console.log(game_view);
				myVue.game_view = game_view;
				showShipsOnGrid(myVue.game_view.ships);
				showSalvoesOnGrid(myVue.game_view.salvoes);
				}
			})
			.catch(function (error) {
				alert(error);
			});
			
	},
			// testAddShips() is a function to test adding Ships in the back end.
			// and has to be deleted when drag and drop is working
		testAddShips: function(){
			fetch('/api/games/players/' + gp + '/ships', {
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json'
        },
        method: 'POST',
        body: JSON.stringify([/*{type: "carrier", locations: ["D3","E3","F3","G3","H3"]},
															{type: "patrol_boat", locations: ["B6", "B7"]},*/
															{type: "destroyer", locations: ["A5","A6","A7"]},
															{type: "submarine", locations: ["I7","I8","I9"]},		
															{type: "battleship", locations: ["D2","E2","F2","G2"]}
														 ])
    		})				
			.then(function(response){				
				alert("Add Ships status" + response.status);
				return response.json();
			})
    	.then(function (data) {			
					document.location.href='/web/game.html?gp=' + gp;
			})
    	.catch(function (error) {
        console.log('Request failure: ', error);
			});
			
		},
		postShips: function(){
			fetch('/api/games/players/' + gp + '/ships', {
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json'
        },
        method: 'POST',
        body: JSON.stringify()
    		})				
			.then(function(response){				
				alert("Add Ships status" + response.status);
				return response.json();
			})
    	.then(function (data) {			
					document.location.href='/web/game.html?gp=' + gp;
			})
    	.catch(function (error) {
        console.log('Request failure: ', error);
			});
			
		},
		rotateShips: function(){
			if(this.horizontal){
				document.getElementById("shipsToBePlaced").classList.toggle("row");
				for( var i = 0; i < this.allships.length; i++){
					var ship = document.getElementById(this.allships[i]);
					console.log(ship);
					ship.setAttribute("class", this.allships[i] + "DraggableVertical mt-2 mr-2");
				}
				this.horizontal = false;
			}
			else{
				document.getElementById("shipsToBePlaced").classList.toggle("row");
				for( var i = 0; i < this.allships.length; i++){
					var ship = document.getElementById(this.allships[i]);
					ship.setAttribute("class", this.allships[i] + "Draggable my-2");
			}
				this.horizontal = true;
		}
		},
			//drag&drop methods
		dragStart: function(ev) {
			console.log("The dragStart 'ev' is: ");
			console.log(ev);
  		ev.dataTransfer.setData("text", ev.target.id);
  		ev.dataTransfer.setData("size", ev.target.dataset.length);
			//console.log("la longitud es: " + ev.target.dataset.length);
		},
		allowDrop: function(ev) {
  		ev.preventDefault();			
		},
		drop: function(ev) {
			console.log("The drop 'ev' is: ");
			console.log(ev);
			//console.log(ev.target.id.slice(4,7));
			//console.log(typeof ev.target.id.slice(5,7));
			//alert("mira el ev en la consola");
  		ev.preventDefault();
  		this.droppedId = ev.dataTransfer.getData("text");
			console.log("The droppedId is: " + myVue.droppedId);
  		this.droppedSize = ev.dataTransfer.getData("size");
			console.log("The droppedSize is: " + myVue.droppedSize);
  		//ev.target.appendChild(document.getElementById(this.droppedId));
			document.getElementById(this.droppedId).style.visibility="hidden";			
			
			for(var i = 0 ; i < this.droppedSize ; i++){
				//ev.target.id.slice(4,7)
				//var letterId = ev.target.id.slice(4, 5);				
				if(this.horizontal){
					var letterId = ev.target.id.slice(4, 5);
					var numberId = parseInt(ev.target.id.slice(5,7)) + i;					
				}
				else{
					// pintar celdas en vertical...
					// ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"]
					var letterIndex = rowLetters.indexOf(ev.target.id.slice(4, 5))
					var letterId = rowLetters[letterIndex +i];
					var numberId = parseInt(ev.target.id.slice(5,7));					
				}
				var cellId = document.getElementById("ship" + letterId + numberId);
					console.log("letter: " + letterId + " + number: " + numberId);
					console.log(cellId);
					cellId.setAttribute("class", myVue.droppedId + "_onGrid");
					cellId.setAttribute("data-shiptype", this.droppedId);
			}
		}
	},
	created: function(){	
		this.getGameView();
	},
	computed: {}
});

// ====================== FUNCTIONS ======================

function renderTable(cellIdPrefix, tableId){
	document.getElementById(tableId).innerHTML = "<tr><td></td>" + colNumbers.map(col => "<td>" + col + "</td>").join("") + "</tr>" + rowLetters.map(row => "<tr><td>" + row + "</td>" + colNumbers.map(col => "<td id='" + cellIdPrefix + row + col + "' v-on:dragover='allowDrop(this.event)' v-on:click='alertSomething(\"dragleave\")' v-on:drop='drop(this.event)'>" + "</td>").join("") + "</tr>").join("");
}

function showShipsOnGrid(ships) {
	ships.forEach(a => a.locations.forEach(b => {
	document.getElementById("ship" + b).setAttribute("class", a.type + "_onGrid");
	document.getElementById("ship" + b).innerHTML = a.type.charAt(0).toUpperCase();
	})
	);
}

function showSalvoesOnGrid(salvoes){
	//this functions will show/render salvoes for currentGp and his/her opponentGp
	salvoes.forEach(salvo => {
		if(salvo.gamePlayer == gp){
			//console.log("imprimiendo salvoes del currentGp");
			//salvo.locations.forEach(location => console.log(location + " " + typeof(location)));			
			salvo.locations.forEach(location => document.getElementById("salvo" + location).classList.add("salvoes"));
		}
		else{
			//console.log("imprimiendo salvoes del opponentGp");
			salvo.locations.forEach(location => document.getElementById("ship" + location).classList.add("salvoes"));
		}
	});
}











