import React from "react";
import './index.css'

function NewModule(){

    async function handleSubmit(event){
        console.log("inside submit function");
try{
    event.preventDefault();
    const moduleNameValue = event.target.modulename.value;
    const response = await fetch(
        "http://localhost:8083/api/v1/modules",
        {
            method:'POST',
            body:
            JSON.stringify(
            {
                ModuleName:moduleNameValue,
                ApiName:moduleNameValue
            }
        ),
        headers: {
                        'Content-Type': 'application/json',
                    }
        }
    );
console.log(response);
    return response;
}
catch(e){
    return e;
}

    }

    return(
<div className="main-content-area"> 
            
            <div className="form-card">
                <h2>Create New Module</h2>
        <form onSubmit={handleSubmit}>



<label htmlFor="modulename">Module name:</label>

  <input type="text" id="modulenameid" placeholder="e.g., Projects, Tasks"
                        className="form-input" name="modulename"/><br/><br/>

<button type="submit" className="submit-button">submit</button>

</form>
</div>
</div>

    );
}

export default NewModule;