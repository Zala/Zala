$("#searchQuery").select2({
    tags:["vzig", "motor", "ropota", "crknu avto", "ne vzge", "ugasnil", "pocila guma", "spuscena guma", 
    "zmanjkalo goriva", "cudni zvoki", "cvili", "dim", "kolo zablokira", "zavore zablokirale", "zaklenjeni kljuci",
    "izgubljeni kljuci", "avto na tleh", "gori lucka za akumulator", "gori lucka", "ne gre v prestavo", "menjalnik",
    "lucka se je prizgala", "nesreca"],
    width: 550,
    maximumSelectionSize: 3,
    tokenSeparators: [",", " "]});


function displayResult(){
    var x=document.getElementsById("searchQuery").value;
    alert(x);
}


function queryQuestions(keywds) {
    $.ajax({
        url:"../EventDescriptionServlet",
        data: {a: keywds},
        type: 'GET',
        dataType: 'text',
        success:function(result){
            alert(result);
        }
    }); 
}