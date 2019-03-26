$(function() {

  // display text in the output area
  function showOutput(text) {
    $("#output").text(text);
  }

  // load and display JSON sent by server for /players

  function loadData() {
    $.get("http://localhost:8080/players")
    .done(function(data) {
      showOutput(JSON.stringify(data, null, 2));
    })
    .fail(function( jqXHR, textStatus ) {
      showOutput( "Failed: " + textStatus );
    });
  }

  // handler for when user clicks add person

  function addPlayer() {
    var name = $("#email").val();
    var firstName = $("#firstName").val();
    var lastName = $("#lastName").val();

    if (name && firstName && lastName) {
      postPlayer(name, firstName, lastName);
    }
  }

  // code to post a new player using AJAX
  // on success, reload and display the updated data from the server

  function postPlayer(userName, firstName, lastName) {
    $.post({
      headers: {
          'Content-Type': 'application/json'
      },
      dataType: "text",
      url: "http://localhost:8080/players",
      data: JSON.stringify({ "userName": userName, "firstName": firstName, "lastName": lastName})
    })
    .done(function( ) {
      showOutput( "Saved -- reloading");
      loadData();
    })
    .fail(function( jqXHR, textStatus ) {
      showOutput( "Failed: " + textStatus );
    });
  }

  $("#add_player").on("click", addPlayer);

  loadData();
});