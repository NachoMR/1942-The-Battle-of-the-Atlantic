//Creation of the two grids (ship's location and Salvoes)
var colNumbers = ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10"];
var rowLetters = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"];

var headerRow = "<tr><td></td>" + colNumbers.map(col => "<td>" + col + "</td>").join("") + "</tr>";
var letterRows = rowLetters.map(row => "<tr><td>" + row + "</td>" + colNumbers.map(col => "<td id='" + row + col + "'>" + "·" + "</td>").join("") + "</tr>").join("");

document.getElementById("shipLocations").innerHTML = headerRow + letterRows;
//document.getElementById("salvoesLocations").innerHTML = headerRow + letterRows;

//getting the gameplayer id from the "query string" at the end of the URL http://localhost:8080/web/game.html?gp=3
var gpNumber = window.location.search.split("=")[1]
console.log("Vamos a hacer Fetch sobre: http://localhost:8080/api/game_view/" + gpNumber);

//================ VUE VAR DECLARATION ===================

var myVue = new Vue({
	el: "#app",
	data: {
		contentVisible: false,
		game_view: [],
	},
	methods: {

	},
	created: function () {
		fetch('http://localhost:8080/api/game_view/' + gpNumber, {
				method: 'GET'
			})
			.then(function (response) {
				return response.json();
			})
			.then(function (game_view) {
				console.log(game_view);
				myVue.game_view = game_view;
				myVue.contentVisible = true;
				showShips(myVue.game_view.ships);
			})
			.catch(function (error) {
				alert(error);
			});
	},

	computed: {
		renderSalvoes: function () {
			/*to be defined later on when we implement salvoes for currrentPlayer*/
		}
	}
});

// ====================== FUNCTIONS ======================


function showShips(ships) {
//	var ships = myVue.game_view.ships;
	//console.log("aaaaaaaaaaa", JSON.stringify(ships))
	ships.forEach(a => a.locations.forEach(b => {
	//console.log(a.type + " " + b)
	console.log(b)
	document.getElementById(b).setAttribute("class", a.type)
	})
	);
}
