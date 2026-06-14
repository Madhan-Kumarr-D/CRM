import React, { useEffect, useState } from "react"; // Added { useState } for cleaner usage
import './index.css'
import {Link} from 'react-router-dom';

function Index() {
  const [SelectedModule,SetSelectedModule] = useState(null);
  const [ModuleList,SetModuleList] = useState([]);
  useEffect(()=>{
    const GetAllModules = async ()=>{
      try{
        const response = await fetch("http://localhost:8083/api/v1/modules",{
        method : 'GET'
      });
      const data = await response.json();
      SetModuleList(data);
      console.log(data);
    
    }
      catch(Exception){
        return Exception;
      }
    };
    GetAllModules();
    },[]);

  return (
    <div className="topnav" >
      <section>
        {ModuleList.map((module)=>{
          return (
   <>
    <a 
      key={module.recordID} 
      href={`/${module.ApiName}`}
      onClick={()=>{SetSelectedModule(module.recordID)}}
      className={(SelectedModule === module.recordID)?"active":""} 
    >
      {module.moduleName}
    </a>
    
</>
  );
        })}
        {/* <a href="/newmodule">New Module</a> */}
        <Link to="/newmodule">New Module</Link>
      </section>
    </div>
  );
}

export default Index;