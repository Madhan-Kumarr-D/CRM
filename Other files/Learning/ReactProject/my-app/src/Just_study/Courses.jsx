import { useState } from 'react'
import img from 'C:/Users/Lenovo/OneDrive/Desktop/ReactProject/my-app/src/assets/react.svg'


function Course(props){

const [price,SetPrice] = useState(props.price);

function HandleClick(buttonData){
    alert(buttonData+" successfully purchased")
}
function HandleDiscount(){
    SetPrice(price *0.91);
    alert("changed")
}
    return (
            <div className="card">
            <img src={props.image}></img>
            <h2>Course - {props.name}</h2>
            <p>Price - {price}</p>
            <button onClick={()=>HandleDiscount(props)}>Apply discount</button>
            <button onClick={()=>props.delete(props.id)}>Delete</button>
            <button onClick={(event)=> HandleClick(event)}>Buy now</button>
        </div>
    )
}

Course.defaultProps = {
    image: img
}
export default Course