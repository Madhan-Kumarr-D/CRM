 html_event = document.getElementById("submit");
 let input = document.getElementById("input");
 let list = document.getElementById("list");

 let tasks = new Array();

 html_event.addEventListener('click',(event)=>{

tasks.push(input.value);
list.innerHTML+= '<li id = "'+(tasks.length-1)+'">'+input.value+'</li>'
input.value='';
console.log(tasks);
 })