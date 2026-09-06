import { useEffect, useState } from 'react';
import html from './assets/react.svg'
import './App.css';
let gram ="joulous";
function Griffen(props){
    // let hog = true;
    const [hog,Setdata] = useState(true);
    useEffect(()=>{
        fetch('https://jsonplaceholder.typicode.com/posts').then(response => {console.log(response); return response.json()},[Setdata]).then(data => {console.log(data)})
    });
    function brain(gip){
        console.log("feel bad dont",gip);
        Setdata(false);
    }
    return (
    <div className="card">
        <p>{props.name}</p>
        <p>{props.age}</p>
        
        <img src={props.image}></img>
        <button onClick={(event)=>brain("wre")} >Buy now</button>
    </div>
    );
}

export default Griffen;