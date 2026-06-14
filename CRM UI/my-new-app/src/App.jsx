import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import Index from'./components/index'
import './App.css'
import NewModule from './components/NewModule'
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import ModulePage from './components/ModulePage';
import NewRecord from './components/NewRecord';
import ListRecord from './components/ListRecord';
import EditRecord from './components/NewRecord';
import EditField from './components/ModulePage';



const router = createBrowserRouter(
  [
    {
      path : "/",
    element : <Index/>
  },
  {
    path : "/newmodule",
    element : <NewModule/>
  },
  {
    path : "/modulepage/:ModuleApiName",
    element : <ModulePage/>
  },
  {
    path : "/create/:ModuleApiName",
    element : <NewRecord/>
  },
  {
    path : "/:ModuleApiName",
    element : <ListRecord/>
  },
  {
    path : "/edit/:ModuleApiName/:RecordID", 
    element : <EditRecord/>
  },
  {
    path : "/edit/field/:ModuleApiName/:RecordID", 
    element : <EditField/>
  }
  ]
)

function App() {

  return (
    <RouterProvider router={router}/>
  )
}

export default App

