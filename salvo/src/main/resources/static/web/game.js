//Creation of the two grids (ship's location and Salvoes) by means of function renderTable()
var colNumbers = ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10"];
var rowLetters = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"];
renderTable("ship", "shipLocations");
renderTable("salvo", "salvoesLocations");

//getting the active/current gameplayer id from the "query string" at the end of the URL http://localhost:8080/web/game.html?gp=3
var gp = window.location.search.split("=")[1]
console.log("Vamos a hacer Fetch sobre: http://localhost:8080/api/game_view/" + gp);
console.log("window.location es: " + window.location);



//================ VUE VAR DECLARATION ===================

var myVue = new Vue({
	el: "#app",
	data: {
		game_view: [],
	},
	methods: {
		goToHome: function(){
			document.location.href="/web/games.html"
		},
		getGameView: function () {
		fetch('http://localhost:8080/api/game_view/' + gp, {
				method: 'GET'
			})
			.then(function (response) {
				return response.json();
			})
			.then(function (game_view) {
				if(game_view.error){
					alert(game_view.error);
					window.location.href = "games.html";
				}
			else{
				console.log(game_view);
				myVue.game_view = game_view;
				showShips(myVue.game_view.ships);
				showSalvoes(myVue.game_view.salvoes);
				}
			})
			.catch(function (error) {
				alert(error);
			});
		
	}
		
	},
	created: function(){	
		this.getGameView();
	},
	computed: {}
});

// ====================== FUNCTIONS ======================

function renderTable(cellIdPrefix, tableId){
	document.getElementById(tableId).innerHTML = "<tr><td></td>" + colNumbers.map(col => "<td>" + col + "</td>").join("") + "</tr>" + rowLetters.map(row => "<tr><td>" + row + "</td>" + colNumbers.map(col => "<td id='" + cellIdPrefix + row + col + "'>" + "·" + "</td>").join("") + "</tr>").join("");
}

function showShips(ships) {
	ships.forEach(a => a.locations.forEach(b => {
	document.getElementById("ship" + b).setAttribute("class", a.type)
	})
	);
}

function showSalvoes(salvoes){
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











