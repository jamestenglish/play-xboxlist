;$(function() {
	$(".alert").alert(); 
	
	//code below from:
	//http://blog.evanrosebrook.com/2011/01/using-amazons-autocomplete-suggestions.html
    //http://completion.amazon.com/search/complete?method=completion&q=halo&search-alias=videogames&mkt=1&x=updateISSCompletion&noCacheIE=1295031912518
    var filter = $("#searchbox").autocomplete({
        source: function (request, response) {
            $.ajax({
                url: "http://completion.amazon.com/search/complete",
                type: "GET",
                cache: false,
                dataType: "jsonp",
                success: function (data) {
                    response(data[1]);
                },
                data: {
                    q: request.term,
                    "search-alias": "videogames",
                    mkt: "1",
                    callback: '?'
                }
            });
        }
    });
	 
});