//================ VUE VAR DECLARATION ===================
/*
j.bauer@ctu.gov				24
c.obrian@ctu.gov			42
kim_bauer@gmail.com		kb
t.almeida@ctu.gov			mole
*/

var myVue = new Vue({
	el: "#app",
	data: {
		gamesJson: {},
		leaderboard: [],
		existingUserInput: {userName: "", password: ""},
		newUserInput: {firstName: "", lastName: "", userName: "", password: ""},
		logInVisible: true,
		auth: false
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
				myVue.leaderboard = leaderboard;
				//myVue.gameplayers = leaderboard.gameplayers;
			}).catch(function (error) {
				alert(error);
				//console.log("Error during fetch" + error.message)
			});
		},		
		postUser: function(arg){
			if(arg.userName == "" || arg.password == ""){
				alert("Please enter your user details or Sign Up as a new user");
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
				myVue.auth = true;
				myVue.getGamesInfo()
				alert("Log In Status: " + data.status);
				//window.location.reload();
    })
    .catch(function (error) {
        console.log('Request failure: ', error);
    });
			}
		},		
		postCreateUser: function(){
			
			//first fetch is for creating the new user
			fetch('/api/players', {
                credentials: 'include',
        headers: {
            'Content-Type': 'application/json'
        },
        method: 'POST',
        body: JSON.stringify(myVue.newUserInput)
    })
    .then(function (data) {
        console.log('Request success: ', data);
				alert("Log In Status: " + data.status);
				this.postUser(newUserInput);
				//window.location.reload();
    })
    .catch(function (error) {
        console.log('Request failure: ', error);
    });
		
		//second fetch is for authentication of the this new user
		
			
			
	},		
		postLogOut: function(){
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
				window.location.reload();
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
//myVue.postUser();
//myVue.postCreateUser();


// ====================== FUNCTIONS ======================

/*
function getBody(json) {
    var body = [];
    for (var key in json) {
        var encKey = encodeURIComponent(key);
        var encVal = encodeURIComponent(json[key]);
        body.push(encKey + "=" + encVal);
    }
    return body.join("&");
}
*/

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














