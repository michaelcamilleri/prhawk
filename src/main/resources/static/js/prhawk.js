function getRepos() {
    let username = document.getElementById("username-input").value;
    if (username) {
        $("#username-error").html(""); // clear error
        $("#results").html(""); // clear results
        let reposUrl = "http://localhost:8080/user/" + username;
        if (document.getElementById("list-prs").checked === true) {
            reposUrl += "?listPRs=true";
        }
        document.getElementById("loader").style.visibility = "visible";
        $.ajax({
            url: reposUrl,
            type: "GET",
            success: function(repos) {
                document.getElementById("loader").style.visibility = "hidden";
                $("#results").html(repos);
            },
            error: function(error) {
                console.log(error);
            }
        })
    } else {
        $("#username-error").html("Username is required");
    }
}
