//================ VUE VAR DECLARATION ===================

var myVue = new Vue({
	el: "#app",
	data: {
		gamesJson: {},
		sortedLeaderboard: [],
		existingUserInput: {
			userName: "",
			password: ""
		},
		newUserInput: {
			firstName: "",
			lastName: "",
			userName: "",
			password: ""
		},
		logInVisible: true,
		auth: false,
		modalAlertContent: ""
	},
	methods: {
		gameOutcome: function (game) {
			if (game.scores.length == 1) {
				return "Not started"
			} else if (game.scores[0].score == null || game.scores[1].score == null) {
				return "Not finished"
			} else if (game.scores[0].score.points == 1) {
				return game.gameplayers[0].player.email.split("@")[0]
			} else if (game.scores[1].score.points == 1) {
				return game.gameplayers[1].player.email.split("@")[0]
			} else {
				return "Tied"
			}
		},
		toggle: function () {
			this.logInVisible = !this.logInVisible;
			//console.log("logInVisible: " + this.logInVisible);
		},
		getGamesInfo: function () {
			fetch('/api/games', {
					method: 'GET'
				})
				.then(function (response) {
				console.log(response);
					if (!response.ok) {
						throw new Error("ERROR " + response.status + "  --  Unable to load games info from database");
					}
					return response.json();
					//alert("Game Data Status: " + response.status)
				})
				.then(function (gamesJson) {
					myVue.gamesJson = gamesJson;
					if (myVue.gamesJson.logged_player !== 'guest') {
						myVue.auth = true
					}
				}).catch(function (er) {
					console.log(er.name + " & " + er.message);
					myVue.showModalAlert(er.message);	
					
				});
		},
		getLeaderBoardInfo: function () {
			fetch('/api/leaderboard', {
					method: 'GET'
				})
				.then(function (response) {
					console.log(response);
					if (!response.ok) {
						throw new Error("ERROR " + response.status + "  --  Unable to load leaderboard info from database");
					}
					return response.json();
					//alert("Leaderboard Data Status" + response.status);
				})
				.then(function (leaderboard) {
					myVue.sortedLeaderboard = leaderboard.sort(function (a, b) {
						if (b.total_points - a.total_points != 0) {
							return b.total_points - a.total_points
						} else if (b.total_played - a.total_played != 0) {
							return a.total_played - b.total_played
						} else {
							return b.won.length - a.won.length
						}
					});
				}).catch(function (er) {
					console.log(er.name + " & " + er.message);
					myVue.showModalAlert(er.message);
				});
		},
		logInUser: function (arg) {
			if (arg.userName == "" || arg.password == "") {
				this.showModalAlert("Please enter your email and password");
			} else {
				fetch('/api/login', {
						credentials: 'include',
						headers: {
							'Content-Type': 'application/x-www-form-urlencoded'
						},
						method: 'POST',
						body: getBody(arg)
					})
					.then(function (data) {
						console.log(data);
						if (!data.ok) {
							throw new Error("ERROR " + data.status + "  --  Incorrect Email or Password");
						}
						myVue.auth = true;
						document.location.reload();
					})
					.catch(function (er) {
						console.log(er.name + " & " + er.message);
						myVue.showModalAlert(er.message);
					});
			}
		},
		/*       signUpUser ====> LINE 242 IN SALVOCONTROLLER.java       */
		signUpUser: function () { 
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
				.then(function (response) {
					console.log(response);					

				return response.json();
				}).then(function (data){
					if(data.error){
						myVue.showModalAlert(data.error);
					}
					else{
						//second fetch is for authentication of the this new user	
					myVue.logInUser(myVue.newUserInput);
					//window.location.reload();
					}
				})			
				.catch(function (er) {
					console.log(er.name + " & " + er.message);
					myVue.showModalAlert(er.message);				
				});
		},
		logOutUser: function () {
			fetch('/api/logout', {
					credentials: 'include',
					headers: {
						'Content-Type': 'application/x-www-form-urlencoded'
					},
					method: 'POST',
					//body: getBody(this.existingUserInput)
				})
				.then(function (data) {
					console.log(data);
					if (!data.ok) {
						throw new Error("ERROR " + data.status + "  --  Unable to log out");
					}
					myVue.auth = false;
					myVue.showModalAlert("STATUS " + data.status + "  --  Logged out successfully!");
					$("#exampleModal").on("hide.bs.modal", function(){
						document.location.reload();
					})		
				})
				.catch(function (er) {
					console.log(er.name + " & " + er.message);
					myVue.showModalAlert(er.message);
				});
		},
		/*       joinGame ====> LINE 49 IN SALVOCONTROLLER.java       */
		joinGame: function (gameId) {
			console.log("El game id es: " + gameId);
			fetch('/api/game/' + gameId + '/players', {
					credentials: 'include',
					headers: {
						'Content-Type': 'application/json'
					},
					method: 'POST',
					body: ""
				})
				.then(function (response) {
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
					else{
						document.location.href = "/web/game.html?gp=" + data.gpid;
					}
				})
				.catch(function (er) {
					console.log(er.name + " & " + er.message);
					myVue.showModalAlert(er.message);
				});
		},
		continueGame: function (game) {
			var gpContinueGame = "";
			for (var i = 0; i < game.gameplayers.length; i++) {
				if (game.gameplayers[i].player.id == this.gamesJson.logged_player.pid) {
					gpContinueGame = game.gameplayers[i].id;
					break;
				}
			}
			if (gpContinueGame == "") {
				//alert("You are not a player in this game and are not allowed to enter the game");
				myVue.showModalAlert("You are not a player in this game and are not allowed to enter the game");
				//document.location.href = "/web/games.html";
			} else {
				document.location.href = "/web/game.html?gp=" + gpContinueGame;
			}
		},
		createGame: function () {
			fetch('/api/games', {
					credentials: 'include',
					headers: {
						'Content-Type': 'application/json'
					},
					method: 'POST',
					body: ""
				})
				.then(function (response) {
					console.log(response);
					return response.json();
				})
				.then(function (data) {
					if(data.error){
						myVue.showModalAlert(data.error);
					}
					else{
						var gpJustCreated = data.gpid;	
						document.location.href = "/web/game.html?gp=" + gpJustCreated;
					}
				})
				.catch(function (er) {
					console.log(er.name + " & " + er.message);
					myVue.showModalAlert(er.message);
				});
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
	created: function () {
		this.getGamesInfo();
		this.getLeaderBoardInfo();
	},
	computed: {
		successfulLogIn: function () {
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
		if (key == 'userName' || key == 'password') {
			var encKey = encodeURIComponent(key);
			var encVal = encodeURIComponent(json[key]);
			body.push(encKey + "=" + encVal);
		}
	}
	console.log(body);
	return body.join("&");
}

