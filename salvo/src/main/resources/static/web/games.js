//================ VUE VAR DECLARATION ===================

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
				method: 'GET'
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


// ====================== FUNCTIONS ======================
