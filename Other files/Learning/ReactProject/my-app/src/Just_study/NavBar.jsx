import Course from './Courses';
import img from 'C:/Users/Lenovo/OneDrive/Desktop/ReactProject/my-app/src/assets/react.svg'
import { useState,useEffect, useContext } from 'react';
import { BrowserRouter , createBrowserRouter,Link,Outlet,RouterProvider } from 'react-router-dom';
import Homie from './homie';
import Jing from './jing';
import { createContext } from 'react';
// import { variable } from '../App';
// import { Outlet } from 'react-router-dom';
// export const variable = createContext();
const route = createBrowserRouter([
    {
        path : "/home",
        element : <Homie/>
    },
    {
        path : "/jing",
        element : <Jing/>
    }
]);

function Navbar(){

    const data = useContext(variable);

    const [list,SetList] = useState([
        {
            
                id:1, name : "HTMl", price : 1000
            
        },
        {
            
                id:2, name : "css", price : 200
            
        },
        {
            
               id:3, name : "js", price : 400
            
        }
    ]);
    
    function HandleDelete(id){
        const data = list.filter((x)=> x.id != id );
        SetList(data);
    }

    // useEffect(()=>{
        
    // },)
    // list.sort((x,y)=>{
    //     return x.price-y.price;
    // })

    // let FilteredData = list.filter((x)=>x.price>500 )
    
    const tagList = list.map((course)=><Course key ={course.id } delete = {HandleDelete} name={course.name} price={course.price} image={img} id={course.id}/>)
    return (
        <nav>
            {tagList}<br/><br/><br/><br/>
        </nav>
    )
}

export default Navbar