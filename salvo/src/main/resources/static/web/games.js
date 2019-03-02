//================ VUE VAR DECLARATION ===================

var myVue = new Vue({
	el: "#app",
	data: {
		gamesJson: {},
		sortedLeaderboard: [],
		existingUserInput: {userName: "", password: ""},
		newUserInput: {firstName: "", lastName: "", userName: "", password: ""},
		logInVisible: true,
		auth: false
	},
	methods: {
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
		toggle: function(){
			this.logInVisible = !this.logInVisible;
			//console.log("logInVisible: " + this.logInVisible);
		},		
		getGamesInfo: function(){
				fetch('/api/games', {
				method: 'GET'
			})
			.then(function (response) {
				return response.json();
				//console.log("First .then() works!");
			})
			.then(function (gamesJson) {
				//console.log(JSON.stringify(gamesJson));
				//console.log(gamesJson);
				//console.log("Second .then() works!");
				myVue.gamesJson = gamesJson;
					if(myVue.gamesJson.logged_player !== 'guest'){
						myVue.auth = true
					}
					
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
				//console.log("Leaderboard Json: " + JSON.stringify(leaderboard));
				//console.log(leaderboard);
				//console.log("Second .then() works!");
				myVue.sortedLeaderboard = leaderboard.sort(function(a,b){
					/* ###########################
					Review this code to properly sort the leaderboard table!!
					########################### */
					if(b.total_played <= a.total_played){
						return b.total_points - a.total_points
					}
					else{
						return b.total_points - a.total_points - 1
					}				
				});
					
			}).catch(function (error) {
				alert(error);
				//console.log("Error during fetch" + error.message)
			});
		},		
		logInUser: function(arg){
			if(arg.userName == "" || arg.password == ""){
				alert("Please enter your email and password");
			}
			else{
			fetch('/api/login', {
                credentials: 'include',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        method: 'POST',
        body: getBody(arg)
    })
    .then(function (data) {
        console.log('Request success: ', data);
				alert("Log In Status: " + data.status);
				if(data.status > 100 && data.status < 300){
				myVue.auth = true;
				}
//				else{
//					myVue.existingUserInput = ["repeat", "repeat"];	
//				}
				//we want to refresh the page:
				//myVue.getGamesInfo();
				//myVue.getLeaderBoardInfo();			
				document.location.reload();
    })
    .catch(function (error) {
        console.log('Request failure: ', error);
    });
			}
		},		
		signUpUser: function(){
			
			//first fetch is for creating the new user
			fetch('/api/players', {
                credentials: 'include',
        headers: {
            'Content-Type': 'application/json'
        },
        method: 'POST',
        body: JSON.stringify(myVue.newUserInput)
				//remember: the body in any request must be a string
    })
    .then(function (data) {
        console.log('Request success: ', data);
				alert("Log In Status: " + data.status);
				//second fetch is for authentication of the this new user
				myVue.logInUser(myVue.newUserInput);
				//window.location.reload();
    })
    .catch(function (error) {
        console.log('Request failure: ', error);
    });
		
			
	},		
		logOutUser: function(){
			fetch('/api/logout', {
                credentials: 'include',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        method: 'POST',
        //body: getBody(this.existingUserInput)
    })
    .then(function (data) {
        console.log('Request success: ', data);
				alert("Log In Status: " + data.status);
				myVue.auth = false;
				document.location.reload();
    })
    .catch(function (error) {
        console.log('Request failure: ', error);
    });
		},
		visibleJoinGameButton: function(game){
			if(game.gameplayers.length == 1){
			return true
				 }
		},
		visibleContinueGameButton: function(game){
			if(game.scores[0].score == null || game.scores[1].score == null){
			return true
				 }
		},			
		joinGame: function(){
			
			
			
			return "http://google.com"
		},
		continueGame: function(game){
			var gpContinueGame = "";
			for(var i = 0; i < game.gameplayers.length; i++){
				if(game.gameplayers[i].player.id == this.gamesJson.logged_player.pid){
					gpContinueGame = game.gameplayers[i].id;
					break;
				}
			}
			return "/web/game.html?gpContinueGame=" + gpContinueGame
		},
		gameOver: function(){
			return "/web/games.html"
		},
		createGame: function(){
			fetch('/api/games', {
                credentials: 'include',
       
				headers: {
            'Content-Type': 'application/json'
        },
				
        method: 'POST',
        body: ""
    })
    .then(function (data) {
       return data.json();
    })
			.then(function(data){
				console.log('Request success: '+  data.gpid);
				//alert("New Game created " + data.status);
				var gpJustCreated = data.gpid;
				console.log("El retorno del Post me trae un data.gp = " + gpJustCreated);
				myVue.getGamesInfo();
				window.location.href = "/web/game.html?gp=" + gpJustCreated;
			})
    .catch(function (error) {
        console.log('Request failure: ', error);
    });
		}
	},
	created: function () {
		this.getGamesInfo();
		this.getLeaderBoardInfo();
	},
	computed: {
		successfulLogIn: function(){	
			return this.gamesJson.logged_player == "guest";			
		}
	}
});

//myVue.getGames();
//myVue.logInUser();
//myVue.signUpUser();


// ====================== FUNCTIONS ======================

function getBody(json) {
    var body = [];
    for (var key in json) {
			if(key == 'userName' || key == 'password'){
        var encKey = encodeURIComponent(key);
        var encVal = encodeURIComponent(json[key]);
        body.push(encKey + "=" + encVal);
			}
    }
    return body.join("&");
}

//newUserInput: {firstName: "", lastName: "", userName: "", password: ""},
