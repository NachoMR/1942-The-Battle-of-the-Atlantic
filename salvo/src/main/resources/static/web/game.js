//for(var i = 0; i < 11; i++){	document.getElementById("playerTable").appendChild(document.createElement("div"))
//}

//var text = ["", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"];
//text.forEach(function(el) {
//    var div = document.createElement("div");
//    div.innerHTML = el;
//    document.getElementById("playerTabl").appendChild(div);
//});

var colNumbers = ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10"];
var rowLetters = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"];
//var emptyCell = ["", "", "", "", "", "", "", "", "", "",];

var headerRow = "<tr><td></td>" + colNumbers.map(col => "<td>" + col + "</td>").join("") + "</tr>";
var letterRows = rowLetters.map(row => "<tr><td>" + row + "</td>" + colNumbers.map(col => "<td id="{row+col}">" + row+col + "</td>").join("") + "</tr>").join("");

document.getElementById("playerTable").innerHTML = headerRow + letterRows;
document.getElementById("opponentTable").innerHTML = headerRow + letterRows;


//================ VUE VAR DECLARATION ===================
/*
var myVue = new Vue({
	el: "#app",
	data: {
		games: [],
		gameplayers: []
	},
	methods: {
		//		getGames: 
	},
	created: function () {
		fetch('http://localhost:8080/api/games', {
				method: 'GET',
			})
			.then(function (response) {
				return response.json();
				//console.log("First .then() works!");
			})
			.then(function (games) {
				console.log(JSON.stringify(games));
				//console.log(games);
				console.log("Second .then() works!");
				myVue.games = games;
				myVue.gameplayers = games.gameplayers;
			}).catch(function (error) {
				alert(error);
				//console.log("Error during fetch" + error.message)
			});
	},
	computed: {}
});


//myVue.getGames();

*/
// ====================== FUNCTIONS ======================
