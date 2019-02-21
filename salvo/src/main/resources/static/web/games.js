//================ VUE VAR DECLARATION ===================

var myVue = new Vue({
	el: "#app",
	data: {
		games: [],
		leaderboard: []
		//gameplayers: []
		//leaderboard: []		
	},
	methods: {
		autoComplete: function(arg){
			if(arg.length > 1){
				return arg;
			}
			else{
				arg.push({"id":"N/A","player":{"id":"N/A","email":"N/A"}});
				return arg;
			}
			},
		gameOutcome: function(game){
			if(game.scores.length == 1){
				return "Not started"
			}			
			else if(game.scores[0].score == null || game.scores[1].score == null){
				return "Not finished"
			}
			else if(game.scores[0].score.points == 1){
				return "Player-1"
			}
			else if(game.scores[1].score.points == 1){
				return "Player-2"
			}			
			else{
				return "Tied"
			}
	},
		getGamesInfo: function(){
				fetch('/api/games', {
				method: 'GET'
			})
			.then(function (response) {
				return response.json();
				//console.log("First .then() works!");
			})
			.then(function (games) {
				console.log(JSON.stringify(games));
				//console.log(games);
				//console.log("Second .then() works!");
				myVue.games = games;
				//myVue.gameplayers = games.gameplayers;
			}).catch(function (error) {
				alert(error);
				//console.log("Error during fetch" + error.message)
			});
		},
		getLeaderBoardInfo: function(){
				fetch('/api/leaderboard', {
				method: 'GET'
			})
			.then(function (response) {
				return response.json();
				//console.log("First .then() works!");
			})
			.then(function (leaderboard) {
				console.log("Leaderboard Json: " + JSON.stringify(leaderboard));
				//console.log(leaderboard);
				//console.log("Second .then() works!");
				myVue.leaderboard = leaderboard;
				//myVue.gameplayers = leaderboard.gameplayers;
			}).catch(function (error) {
				alert(error);
				//console.log("Error during fetch" + error.message)
			});
		},
		signUp: function(){
			console.log("signUp method running");
		},
		logIn: function(){
			console.log("logIn method running");
		}		
	},
	created: function () {	
		this.getGamesInfo();
		this.getLeaderBoardInfo();		
	},
	computed: {}
});


//myVue.getGames();


// ====================== FUNCTIONS ======================




/*
function leaderboard(){
	var leaderboard = [
		{name: ???,
		total: ???,
		won: ???,}
	];
	
}
*/











