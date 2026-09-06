import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'
import Maddy from './maddy'
import Griffen from './Griffen'
import react from './assets/react.svg'
import javascript from './assets/javascript.png'
import angular from './assets/angular.png'
import Courselist from './courseList'
function App() {


  return (
    <>
    <Griffen name = "react" age = "Price : 400" image = {react}/>
    <Griffen name = "javascript" age = "Price : 800" image = {javascript}/>
    <Griffen name = "angular" age = "Price : 1200" image = {angular}/>
    </>
      
      // <Courselist/>
   
  )
}

export default App
